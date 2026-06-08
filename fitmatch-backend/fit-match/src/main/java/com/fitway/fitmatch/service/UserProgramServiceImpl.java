package com.fitway.fitmatch.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitway.fitmatch.dto.QuestionnaireDTO;
import com.fitway.fitmatch.dto.UserProgramDTO;
import com.fitway.fitmatch.dto.WorkoutDTO;
import com.fitway.fitmatch.entity.NutritionTip;
import com.fitway.fitmatch.entity.ProgramWorkoutStatus;
import com.fitway.fitmatch.entity.User;
import com.fitway.fitmatch.entity.UserProgram;
import com.fitway.fitmatch.entity.Workout;
import com.fitway.fitmatch.entity.enums.ProgramStatus;
import com.fitway.fitmatch.exception.ProgramException;
import com.fitway.fitmatch.repository.NutritionTipRepository;
import com.fitway.fitmatch.repository.ProgramWorkoutStatusRepository;
import com.fitway.fitmatch.repository.UserProgramRepository;
import com.fitway.fitmatch.repository.UserRepository;
import com.fitway.fitmatch.repository.WorkoutRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProgramServiceImpl implements UserProgramService {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final UserProgramRepository userProgramRepository;
    private final NutritionTipRepository nutritionTipRepository;
    private final ProgramWorkoutStatusRepository statusRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public UserProgramDTO createProgramFromQuestionnaire(QuestionnaireDTO questionnaire) {
        User user = userRepository.findById(questionnaire.getUserId())
                .orElseThrow(() -> new ProgramException("משתמש לא נמצא."));

        int totalWorkoutsNeeded = questionnaire.getDaysPerWeek() * questionnaire.getDurationWeeks();
        List<Workout> allWorkouts = workoutRepository.findAll();

        // סינון לפי רמה, מיקומים מרובים, וציוד
        List<Workout> validWorkouts = allWorkouts.stream()
                .filter(w -> w.getDifficultyLevel() == questionnaire.getDifficultyLevel())
                .filter(w -> questionnaire.getPreferredLocations() == null ||
                             questionnaire.getPreferredLocations().isEmpty() ||
                             questionnaire.getPreferredLocations().contains(w.getLocation()))
                .filter(w -> questionnaire.getAvailableEquipmentIds() == null ||
                             questionnaire.getAvailableEquipmentIds().isEmpty() ||
                             w.getRequiredEquipment().stream()
                                .allMatch(eq -> questionnaire.getAvailableEquipmentIds().contains(eq.getId())))
                .toList();

        // מיון לפי התאמת חלקי גוף וקרבה ליעד קלורי רצוי לאימון
        int desiredPerWorkout = Math.max(1, questionnaire.getWeeklyCaloriesGoal() / Math.max(1, questionnaire.getDaysPerWeek()));
        List<Workout> sortedWorkouts = validWorkouts.stream()
                .sorted(Comparator
                        .comparingLong((Workout w) -> {
                            if (questionnaire.getPreferredBodyPartIds() == null) return 0L;
                            return w.getTargetBodyParts().stream()
                                    .filter(bp -> questionnaire.getPreferredBodyPartIds().contains(bp.getId()))
                                    .count();
                        }).reversed()
                        .thenComparingInt(w -> Math.abs(w.getCaloriesBurned() - desiredPerWorkout)))
                .collect(Collectors.toList());

        // בדיקה: אם אין אף אימון מתאים - לא נוכל לבנות תוכנית. אחרת
        // ניתן להשתמש בחזרה מעגלית על מנת למלא את מספר האימונים הנדרש
        if (sortedWorkouts.isEmpty()) {
            throw new ProgramException(
                "אין לנו אימונים מתאימים עבור ההגדרות שבחרת. " +
                "נסה להרחיב את הגדרות הרמה, המיקום או הציוד.");
        }

        // בחירת אימונים ייחודיים קודם, ועד חזרה רק אם אין מספיק מגוון
        List<Workout> selectedWorkouts = new java.util.ArrayList<>();
        java.util.Set<Long> seenWorkoutIds = new java.util.LinkedHashSet<>();
        for (Workout workout : sortedWorkouts) {
            if (selectedWorkouts.size() >= totalWorkoutsNeeded) break;
            if (seenWorkoutIds.add(workout.getId())) {
                selectedWorkouts.add(workout);
            }
        }

        if (selectedWorkouts.size() < totalWorkoutsNeeded) {
            int idx = 0;
            while (selectedWorkouts.size() < totalWorkoutsNeeded) {
                selectedWorkouts.add(sortedWorkouts.get(idx));
                idx = (idx + 1) % sortedWorkouts.size();
            }
        }

        Optional<UserProgram> activeProgramOpt = userProgramRepository
                .findByUserIdAndStatus(user.getId(), ProgramStatus.ACTIVE);
        ProgramStatus finalStatus = activeProgramOpt.isPresent() ? ProgramStatus.FUTURE : ProgramStatus.ACTIVE;

        UserProgram program = new UserProgram();
        program.setUser(user);
        program.setStartDate(java.time.LocalDate.now());
        program.setDurationWeeks(questionnaire.getDurationWeeks());
        program.setDaysPerWeekTarget(questionnaire.getDaysPerWeek());
        program.setStatus(finalStatus);
        program.setWorkouts(selectedWorkouts);
        int desiredTargetCalories = questionnaire.getWeeklyCaloriesGoal() * questionnaire.getDurationWeeks();
        int actualWorkoutCalories = selectedWorkouts.stream().mapToInt(Workout::getCaloriesBurned).sum();
        program.setTotalTargetCalories(Math.max(desiredTargetCalories, actualWorkoutCalories));
        program.setBurnedCaloriesInProgram(0);

        UserProgram savedProgram = userProgramRepository.save(program);
        return buildProgramDTO(savedProgram);
    }

    @Override
    @Transactional
    public UserProgramDTO getActiveProgramByUserId(Long userId) {
        UserProgram program = userProgramRepository.findByUserIdAndStatus(userId, ProgramStatus.ACTIVE)
                .orElseThrow(() -> new ProgramException(
                        "אין לך כרגע תוכנית אימונים פעילה. מלא את השאלון כדי להתחיל!"));
        return buildProgramDTO(program);
    }

    @Override
    @Transactional
    public List<UserProgramDTO> getAllProgramsByUserId(Long userId) {
        return userProgramRepository.findByUserId(userId).stream()
                .map(this::buildProgramDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserProgramDTO activateFutureProgram(Long programId) {
        UserProgram program = userProgramRepository.findById(programId)
                .orElseThrow(() -> new ProgramException("תוכנית לא נמצאה."));

        if (program.getStatus() != ProgramStatus.FUTURE && program.getStatus() != ProgramStatus.COMPLETED) {
            throw new ProgramException("ניתן להפעיל רק תוכנית עתידית או תוכנית שהושלמה בעבר.");
        }

        Optional<UserProgram> activeProgramOpt = userProgramRepository
                .findByUserIdAndStatus(program.getUser().getId(), ProgramStatus.ACTIVE);

        if (activeProgramOpt.isPresent()) {
            UserProgram activeProgram = activeProgramOpt.get();
            activeProgram.setStatus(ProgramStatus.FUTURE);
            userProgramRepository.save(activeProgram);
        }

        if (program.getStatus() == ProgramStatus.COMPLETED) {
            List<ProgramWorkoutStatus> statuses = statusRepository.findByProgramId(program.getId());
            statuses.forEach(s -> s.setCompleted(false));
            statusRepository.saveAll(statuses);
            program.setBurnedCaloriesInProgram(0);
        }

        program.setStatus(ProgramStatus.ACTIVE);
        program.setStartDate(java.time.LocalDate.now());
        userProgramRepository.save(program);
        return buildProgramDTO(program);
    }

    // מתודה מרוכזת לבניית ה-DTO עם completed flags וטיפים תזונתיים
    private UserProgramDTO buildProgramDTO(UserProgram program) {
        List<ProgramWorkoutStatus> statuses = statusRepository.findByProgramId(program.getId());

        UserProgramDTO dto = mapper.map(program, UserProgramDTO.class);

        java.util.Map<Integer, Boolean> completedBySequence = statuses.stream()
                .collect(java.util.stream.Collectors.toMap(ProgramWorkoutStatus::getSequence,
                        ProgramWorkoutStatus::isCompleted,
                        (first, second) -> first));

        List<WorkoutDTO> workoutDTOs = java.util.stream.IntStream.range(0, program.getWorkouts().size())
                .mapToObj(i -> {
                    Workout w = program.getWorkouts().get(i);
                    WorkoutDTO wDto = mapper.map(w, WorkoutDTO.class);
                    boolean isCompleted = completedBySequence.getOrDefault(i, false);
                    wDto.setCompleted(isCompleted);
                    nutritionTipRepository.findTipForCalories(w.getCaloriesBurned()).ifPresentOrElse(
                            tip -> {
                                wDto.setFoodRecommendation(tip.getFoodRecommendation());
                                wDto.setWaterRecommendation(tip.getWaterRecommendation());
                            },
                            () -> {
                                wDto.setFoodRecommendation("הקפד על שילוב חלבון ופחמימה לאחר האימון.");
                                wDto.setWaterRecommendation("שתה לפחות 2 כוסות מים לאחר האימון.");
                            }
                    );
                    return wDto;
                }).toList();

        dto.setWorkouts(workoutDTOs);
        return dto;
    }
}
