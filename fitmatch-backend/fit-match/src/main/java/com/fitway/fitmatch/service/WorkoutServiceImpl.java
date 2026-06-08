package com.fitway.fitmatch.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitway.fitmatch.dto.WorkoutDTO;
import com.fitway.fitmatch.entity.BodyPart;
import com.fitway.fitmatch.entity.UserProgram;
import com.fitway.fitmatch.entity.Workout;
import com.fitway.fitmatch.entity.enums.DifficultyLevel;
import com.fitway.fitmatch.entity.enums.WorkoutLocation;
import com.fitway.fitmatch.exception.WorkoutException;
import com.fitway.fitmatch.repository.UserProgramRepository;
import com.fitway.fitmatch.repository.WorkoutRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // יצירת בנאי והזרקה אוטומטית של השדות (כמו אצל המורה)
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserProgramRepository userProgramRepository;
    private final com.fitway.fitmatch.repository.BodyPartRepository bodyPartRepository;
    private final com.fitway.fitmatch.repository.EquipmentRepository equipmentRepository;
    private final ModelMapper mapper; // משמש להעברה חלקה בין ישויות ל-DTOs

    @Override
    @Transactional
    public WorkoutDTO swapWorkoutInProgram(Long programId, Long currentWorkoutId) {
        // 1. שליפת התוכנית הנוכחית של המשתמש
        UserProgram program = userProgramRepository.findById(programId)
                .orElseThrow(() -> new WorkoutException("אופס, לא מצאנו את תוכנית האימונים הזו במערכת!"));

        // 2. שליפת האימון שהמשתמש רוצה להחליף
        Workout currentWorkout = workoutRepository.findById(currentWorkoutId)
                .orElseThrow(() -> new WorkoutException("האימון שביקשת להחליף אינו קיים."));

        // 3. שליפת חלקי הגוף של האימון הנוכחי (כפי שציינת, יכולים להיות כמה)
        List<BodyPart> bodyParts = currentWorkout.getTargetBodyParts();
        if (bodyParts.isEmpty()) {
            throw new WorkoutException("לא ניתן להחליף אימון שאין לו חלקי גוף מוגדרים.");
        }

        // נבחר את חלק הגוף הראשון של התרגיל כמייצג לצורך החיפוש החלופי
        Long bodyPartId = bodyParts.get(0).getId();

        // 4. חיפוש אימונים אלטרנטיביים באותה רמה, לאותו שריר, ובלי האימון הנוכחי
        List<Workout> alternatives = workoutRepository.findAlternatives(
                currentWorkout.getDifficultyLevel(),
                bodyPartId,
                currentWorkoutId
        );

        // 5. סינון אימונים שכבר משובצים למשתמש בלו"ז הנוכחי שלו (כדי למנוע כפילויות)
        List<Workout> validAlternatives = alternatives.stream()
                .filter(workout -> !program.getWorkouts().contains(workout))
                .toList();

        // 6. הגנה: אם אין אף אימון חלופי מתאים ב-DB - נזרוק את השגיאה הבדיחתית שהגדרנו!
        if (validAlternatives.isEmpty()) {
            throw new WorkoutException("חיפשנו, הפכנו את כל האינטרנט, אפילו שאלנו את המנהל... " +
                    "אבל אין לנו כרגע עוד אימון מתאים ברמה שלך בנמצא! כנראה שאתה פשוט טוב מדי, אלוף. " +
                    "אין ברירה, הולכים על האימון המקורי בכל הכוח!");
        }

        // 7. בחירת האימון האלטרנטיבי הראשון שנמצא
        Workout newWorkout = validAlternatives.get(0);

        // 8. החלפה בפועל בתוך רשימת האימונים של התוכנית
        List<Workout> currentProgramWorkouts = program.getWorkouts();
        int indexToReplace = currentProgramWorkouts.indexOf(currentWorkout);
        if (indexToReplace != -1) {
            currentProgramWorkouts.set(indexToReplace, newWorkout);
        }

        // 9. שמירת השינוי במסד הנתונים
        userProgramRepository.save(program);

        // 10. החזרת האימון החדש כ-DTO לצד הלקוח
        return mapper.map(newWorkout, WorkoutDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutDTO> getAllWorkouts() {
        return workoutRepository.findAll().stream()
                .map(workout -> mapper.map(workout, WorkoutDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutDTO> getFilteredWorkouts(DifficultyLevel difficulty, WorkoutLocation location) {
        return workoutRepository.findAll().stream()
                .filter(w -> difficulty == null || w.getDifficultyLevel() == difficulty)
                .filter(w -> location == null || w.getLocation() == location)
                .map(workout -> mapper.map(workout, WorkoutDTO.class))
                .toList();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public WorkoutDTO addWorkout(com.fitway.fitmatch.dto.WorkoutCreateDTO dto) {
        Workout workout = new Workout();
        workout.setTitle(dto.getTitle());
        workout.setDescription(dto.getDescription());
        workout.setYoutubeUrl(dto.getYoutubeUrl());
        workout.setDurationMinutes(dto.getDurationMinutes());
        workout.setCaloriesBurned(dto.getCaloriesBurned());
        workout.setDifficultyLevel(dto.getDifficultyLevel());
        workout.setLocation(dto.getLocation());

        if (dto.getBodyPartIds() != null && !dto.getBodyPartIds().isEmpty()) {
            workout.setTargetBodyParts(bodyPartRepository.findAllById(dto.getBodyPartIds()));
        }
        if (dto.getEquipmentIds() != null && !dto.getEquipmentIds().isEmpty()) {
            workout.setRequiredEquipment(equipmentRepository.findAllById(dto.getEquipmentIds()));
        }

        Workout saved = workoutRepository.save(workout);
        workout.setId(saved.getId());
        return mapper.map(workout, WorkoutDTO.class);
    }
}