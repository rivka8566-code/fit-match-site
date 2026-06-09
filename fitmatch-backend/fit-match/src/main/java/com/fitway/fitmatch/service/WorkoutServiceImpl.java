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
    private final com.fitway.fitmatch.repository.NutritionTipRepository nutritionTipRepository;
    private final ModelMapper mapper; // משמש להעברה חלקה בין ישויות ל-DTOs

    @Override
    @Transactional
    public WorkoutDTO swapWorkoutInProgram(Long programId, Long currentWorkoutId) {
        UserProgram program = userProgramRepository.findById(programId)
                .orElseThrow(() -> new WorkoutException("אופס, לא מצאנו את תוכנית האימונים הזו במערכת!"));

        Workout currentWorkout = workoutRepository.findById(currentWorkoutId)
                .orElseThrow(() -> new WorkoutException("האימון שביקשת להחליף אינו קיים."));

        List<BodyPart> bodyParts = currentWorkout.getTargetBodyParts();
        if (bodyParts.isEmpty()) {
            throw new WorkoutException("לא ניתן להחליף אימון שאין לו חלקי גוף מוגדרים.");
        }

        Long bodyPartId = bodyParts.get(0).getId();

        List<Workout> alternatives = workoutRepository.findAlternatives(
                currentWorkout.getDifficultyLevel(),
                bodyPartId,
                currentWorkoutId);

        List<Workout> validAlternatives = alternatives.stream()
                .filter(workout -> !program.getWorkouts().contains(workout))
                .toList();

        if (validAlternatives.isEmpty()) {
            throw new WorkoutException("חיפשנו, הפכנו את כל האינטרנט, אפילו שאלנו את המנהל... " +
                    "אבל אין לנו כרגע עוד אימון מתאים ברמה שלך בנמצא! כנראה שאתה פשוט טוב מדי, אלוף. " +
                    "אין ברירה, הולכים על האימון המקורי בכל הכוח!");
        }

        Workout newWorkout = validAlternatives.get(0);

        List<Workout> currentProgramWorkouts = program.getWorkouts();
        int indexToReplace = currentProgramWorkouts.indexOf(currentWorkout);
        if (indexToReplace != -1) {
            currentProgramWorkouts.set(indexToReplace, newWorkout);
        }

        userProgramRepository.save(program);

        return convertToDtoWithTips(newWorkout);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutDTO> getAllWorkouts() {
        return workoutRepository.findAll().stream()
                .map(this::convertToDtoWithTips)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutDTO> getFilteredWorkouts(DifficultyLevel difficulty, WorkoutLocation location) {
        return workoutRepository.findAll().stream()
                .filter(w -> difficulty == null || w.getDifficultyLevel() == difficulty)
                .filter(w -> location == null || w.getLocation() == location)
                .map(this::convertToDtoWithTips)
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
        return convertToDtoWithTips(workout);
    }

    private WorkoutDTO convertToDtoWithTips(Workout workout) {
        WorkoutDTO dto = mapper.map(workout, WorkoutDTO.class);

        nutritionTipRepository.findTipForCalories(workout.getCaloriesBurned()).ifPresent(tip -> {
            dto.setFoodRecommendation(tip.getFoodRecommendation());
            dto.setWaterRecommendation(tip.getWaterRecommendation());
        });

        return dto;
    }
}