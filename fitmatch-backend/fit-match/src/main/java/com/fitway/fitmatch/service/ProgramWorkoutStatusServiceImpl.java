package com.fitway.fitmatch.service;

import com.fitway.fitmatch.dto.UserProgramDTO;
import com.fitway.fitmatch.dto.WorkoutDTO;
import com.fitway.fitmatch.entity.ProgramWorkoutStatus;
import com.fitway.fitmatch.entity.UserProgram;
import com.fitway.fitmatch.entity.Workout;
import com.fitway.fitmatch.exception.ProgramException;
import com.fitway.fitmatch.exception.WorkoutException;
import com.fitway.fitmatch.repository.NutritionTipRepository;
import com.fitway.fitmatch.repository.ProgramWorkoutStatusRepository;
import com.fitway.fitmatch.repository.UserProgramRepository;
import com.fitway.fitmatch.repository.WorkoutRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramWorkoutStatusServiceImpl implements ProgramWorkoutStatusService {

    private final ProgramWorkoutStatusRepository statusRepository;
    private final UserProgramRepository userProgramRepository;
    private final WorkoutRepository workoutRepository;
    private final NutritionTipRepository nutritionTipRepository;
    private final UserService userService;
    private final ModelMapper mapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public UserProgramDTO markWorkoutCompleted(Long programId, Long workoutId, Integer sequence) {
        UserProgram program = userProgramRepository.findById(programId)// שומר את התוכנית המלאה עפ"י ה-ID שלה.
                .orElseThrow(() -> new ProgramException("תוכנית לא נמצאה."));

        Workout workout = workoutRepository.findById(workoutId)// שומר את האימון המלא עפ"י ה-ID שלו.
                .orElseThrow(() -> new WorkoutException("האימון לא נמצא."));

        List<ProgramWorkoutStatus> statuses = statusRepository.findByProgramId(programId);// שומר את כל הסטטוסים של
                                                                                          // האימונים בתוכנית זו.
        int daysPerWeek = program.getDaysPerWeekTarget();
        List<Workout> programWorkouts = program.getWorkouts();// האימונים של התוכנית בצורת רשימה, לפי הסדר שלהם בתוכנית.

        int workoutIndex = -1;
        if (sequence != null) {
            if (sequence < 0 || sequence >= programWorkouts.size()) {
                throw new WorkoutException("מספר האימון לא תקף.");
            }
            if (!programWorkouts.get(sequence).getId().equals(workoutId)) {
                throw new WorkoutException("האימון שנבחר אינו תואם למיקום בתוכנית.");
            }
            workoutIndex = sequence;
        } else {
            for (int i = 0; i < programWorkouts.size(); i++) {
                if (programWorkouts.get(i).getId().equals(workoutId)) {
                    final int idx = i;
                    boolean alreadyCompleted = statuses.stream()
                            .anyMatch(s -> s.getSequence() == idx && s.isCompleted());
                    if (!alreadyCompleted) {
                        workoutIndex = i;
                        break;
                    }
                }
            }
        }

        if (workoutIndex == -1) {
            throw new WorkoutException("האימון אינו חלק מתוכנית זו או כבר הושלם.");
        }

        int currentWeek = workoutIndex / daysPerWeek;
        long completedThisWeek = 0;
        for (int i = currentWeek * daysPerWeek; i < Math.min((currentWeek + 1) * daysPerWeek,
                programWorkouts.size()); i++) {// בודק כמה אימונים כבר הושלמו בשבוע הנוכחי של התוכנית.
            final int idx = i;
            if (statuses.stream().anyMatch(s -> s.getSequence() == idx && s.isCompleted())) {// אם האימון הושלם, מגדיל
                                                                                             // את הספירה של האימונים
                                                                                             // שהושלמו השבוע.
                completedThisWeek++;
            }
        }

        if (completedThisWeek >= daysPerWeek) {// אם כבר הושלמו מספיק אימונים לשבוע זה, זורק חריגה שמודיעה למשתמש שהוא
                                               // לא יכול להשלים עוד אימון השבוע.
            throw new WorkoutException("כבר השלמת את מכסת האימונים לשבוע זה. חכה לשבוע הבא!");
        }

        ProgramWorkoutStatus status = statusRepository// מוצא את הסטטוס של האימון בתוכנית לפי ה-ID של התוכנית ומספר
                                                      // האימון בסדר השבועי. אם לא קיים, יוצר סטטוס חדש עם סטטוס לא
                                                      // הושלם.
                .findByProgramIdAndSequence(programId, workoutIndex)
                .orElse(new ProgramWorkoutStatus(null, programId, workoutId, workoutIndex, false));

        if (!status.isCompleted()) {
            status.setCompleted(true);
            statusRepository.save(status);

            program.setBurnedCaloriesInProgram(
                    program.getBurnedCaloriesInProgram() + workout.getCaloriesBurned());

            userService.addCaloriesToUser(program.getUser().getId(), workout.getCaloriesBurned());

            long totalCompleted = statusRepository.findByProgramId(programId).stream()
                    .filter(ProgramWorkoutStatus::isCompleted).count();

            if (totalCompleted >= programWorkouts.size()) {
                program.setStatus(com.fitway.fitmatch.entity.enums.ProgramStatus.COMPLETED);
                userProgramRepository.saveAndFlush(program);
                System.out.println("🎉 המשתמש סיים את כל האימונים בתוכנית! הסטטוס שונה ל-COMPLETED.");
            } else {
                userProgramRepository.saveAndFlush(program);
            }
        }

        entityManager.clear();

        UserProgram refreshed = userProgramRepository.findById(programId)
                .orElse(program);
        return buildProgramDTO(refreshed);
    }

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
                    nutritionTipRepository.findTipForCalories(w.getCaloriesBurned()).ifPresent(tip -> {
                        wDto.setFoodRecommendation(tip.getFoodRecommendation());
                        wDto.setWaterRecommendation(tip.getWaterRecommendation());
                    });
                    return wDto;
                }).toList();

        dto.setWorkouts(workoutDTOs);
        return dto;
    }
}
