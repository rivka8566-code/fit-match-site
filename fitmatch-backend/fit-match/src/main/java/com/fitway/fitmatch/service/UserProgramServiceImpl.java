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

        List<Workout> allWorkouts = workoutRepository.findAll();

        // 1. סינון בסיסי לפי רמה, מיקום וציוד
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

        if (validWorkouts.isEmpty()) {
            throw new ProgramException("אין לנו אימונים מתאימים עבור ההגדרות שבחרת. נסה להרחיב את הגדרות הרמה, המיקום או הציוד.");
        }

        // 2. פיצול המאגר לאימונים מועדפים (לפי חלקי גוף) ואימונים רגילים לצורך גיוון חכם
        List<Workout> preferredPool = new java.util.ArrayList<>();
        List<Workout> generalPool = new java.util.ArrayList<>();

        for (Workout w : validWorkouts) {
            boolean matchesBodyPart = questionnaire.getPreferredBodyPartIds() != null &&
                    w.getTargetBodyParts().stream().anyMatch(bp -> questionnaire.getPreferredBodyPartIds().contains(bp.getId()));
            
            if (matchesBodyPart) {
                preferredPool.add(w);
            } else {
                generalPool.add(w);
            }
        }

        // מערבבים את שני המאגרים בצורה רנדומלית לחלוטין!
        java.util.Collections.shuffle(preferredPool);
        java.util.Collections.shuffle(generalPool);

        // מחברים אותם כך שהמועדפים יהיו בהתחלה אבל בסדר אקראי, והשאר יפתחו לגיוון בסוף
        List<Workout> finalPool = new java.util.ArrayList<>();
        finalPool.addAll(preferredPool);
        finalPool.addAll(generalPool);

        // 3. שיבוץ אימונים בשיטת "חפיסת קלפים" למניעת כפילויות מוחלטת
        List<Workout> selectedWorkouts = new java.util.ArrayList<>();
        int totalWorkoutsNeeded = questionnaire.getDaysPerWeek() * questionnaire.getDurationWeeks();
        
        // מייצרים את "ערימת הקלפים" הנוכחית שלנו ומערבבים אותה
        List<Workout> cardDeck = new java.util.ArrayList<>(finalPool);
        java.util.Collections.shuffle(cardDeck);
        
        for (int i = 0; i < totalWorkoutsNeeded; i++) {
            // אם ניצלנו את כל האימונים שבערימה, נמלא אותה מחדש ונערבב שוב בסדר אחר!
            if (cardDeck.isEmpty()) {
                cardDeck.addAll(finalPool);
                java.util.Collections.shuffle(cardDeck);
            }
            
            // שולפים את האימון הראשון מהערימה המעורבבת ומסירים אותו כדי שלא יחזור מיד
            Workout workoutToSchedule = cardDeck.remove(0);
            selectedWorkouts.add(workoutToSchedule);
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
        
        // השרת פשוט מחשב את סך הקלוריות האמיתי שיצא מהאימונים המגוונים האלו ושומר אותו
        int actualWorkoutCalories = selectedWorkouts.stream().mapToInt(Workout::getCaloriesBurned).sum();
        program.setTotalTargetCalories(actualWorkoutCalories); 
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
                            });
                    return wDto;
                }).toList();

        dto.setWorkouts(workoutDTOs);
        return dto;
    }
}