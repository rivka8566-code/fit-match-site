package com.fitway.fitmatch.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitway.fitmatch.dto.QuestionnaireDTO;
import com.fitway.fitmatch.dto.UserProgramDTO;
import com.fitway.fitmatch.dto.WorkoutDTO;
import com.fitway.fitmatch.entity.NutritionTip;
import com.fitway.fitmatch.entity.User;
import com.fitway.fitmatch.entity.UserProgram;
import com.fitway.fitmatch.entity.Workout;
import com.fitway.fitmatch.entity.enums.ProgramStatus;
import com.fitway.fitmatch.exception.ProgramException;
import com.fitway.fitmatch.repository.NutritionTipRepository;
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
    private final ModelMapper mapper;

    @Override
    @Transactional
    public UserProgramDTO createProgramFromQuestionnaire(QuestionnaireDTO questionnaire) {
        User user = userRepository.findById(questionnaire.getUserId())
                .orElseThrow(() -> new ProgramException("משתמש לא נמצא."));

        int totalWorkoutsNeeded = questionnaire.getDaysPerWeek() * questionnaire.getDurationWeeks();
        List<Workout> allWorkouts = workoutRepository.findAll();

        // 1. סינון האימונים לפי רמה, מיקום והתאמת ציוד מלאה
        List<Workout> validWorkouts = allWorkouts.stream()
                .filter(w -> w.getDifficultyLevel() == questionnaire.getDifficultyLevel())
                .filter(w -> w.getLocation() == questionnaire.getPreferredLocation())
                .filter(w -> w.getRequiredEquipment().stream()
                        .allMatch(eq -> questionnaire.getAvailableEquipmentIds().contains(eq.getId())))
                .toList();

        // מיון האימונים לפי כמות ההתאמות לחלקי הגוף שהמשתמש ביקש
        List<Workout> sortedWorkouts = validWorkouts.stream()
                .sorted((w1, w2) -> {
                    long w1Matches = w1.getTargetBodyParts().stream()
                            .filter(bp -> questionnaire.getPreferredBodyPartIds().contains(bp.getId())).count();
                    long w2Matches = w2.getTargetBodyParts().stream()
                            .filter(bp -> questionnaire.getPreferredBodyPartIds().contains(bp.getId())).count();
                    return Long.compare(w2Matches, w1Matches);
                })
                .limit(totalWorkoutsNeeded)
                .toList();

        // מנגנון הגנה במידה ובנק הסרטונים קטן מדי
        if (sortedWorkouts.isEmpty()) {
            sortedWorkouts = allWorkouts.stream()
                    .filter(w -> w.getDifficultyLevel() == questionnaire.getDifficultyLevel())
                    .limit(totalWorkoutsNeeded)
                    .toList();
        }

        // ---------------- התיקון של הבדיקה הניהולית ----------------
        // בדיקה האם למשתמש יש כבר תוכנית שהיא כרגע במצב פעיל (ACTIVE)
        Optional<UserProgram> activeProgramOpt = userProgramRepository.findByUserIdAndStatus(user.getId(), ProgramStatus.ACTIVE);
        
        // קביעת הסטטוס: אם יש לו כבר תוכנית פעילה, החדשה תמתין בתור כ-FUTURE. אחרת, היא תהיה ה-ACTIVE הנוכחית.
        ProgramStatus finalStatus = activeProgramOpt.isPresent() ? ProgramStatus.FUTURE : ProgramStatus.ACTIVE;
        // -----------------------------------------------------------

        // 2. בניית אובייקט התוכנית ושמירתו במסד הנתונים
        UserProgram program = new UserProgram();
        program.setUser(user);
        program.setStartDate(questionnaire.getPreferredStartDate());
        program.setDurationWeeks(questionnaire.getDurationWeeks());
        program.setDaysPerWeekTarget(questionnaire.getDaysPerWeek());
        program.setStatus(finalStatus); // שימוש בסטטוס המחושב והמוגן
        program.setWorkouts(sortedWorkouts);
        program.setTotalTargetCalories(sortedWorkouts.stream().mapToInt(Workout::getCaloriesBurned).sum());

        UserProgram savedProgram = userProgramRepository.save(program);

        // 3. מיפוי ל-DTO והזרקת טיפי התזונה לכל אימון בנפרד (קוד נקי ומאוחד)
        UserProgramDTO responseDTO = mapper.map(savedProgram, UserProgramDTO.class);
        
        // במקום לפצל את המיפוי ידנית, אנחנו שולפים את רשימת ה-Workouts שכבר עברה מיפוי בתוך ה-responseDTO
        List<WorkoutDTO> workoutDTOs = responseDTO.getWorkouts();

        // לולאה שעוברת על כל WorkoutDTO ומתאימה לו את הטיפים התזונתיים
        if (workoutDTOs != null) {
            for (WorkoutDTO wDto : workoutDTOs) {
                Optional<NutritionTip> tip = nutritionTipRepository.findTipForCalories(wDto.getCaloriesBurned());
                if (tip.isPresent()) {
                    wDto.setFoodRecommendation(tip.get().getFoodRecommendation());
                    wDto.setWaterRecommendation(tip.get().getWaterRecommendation());
                } else {
                    wDto.setFoodRecommendation("אימון מעולה! הקפד על שילוב חלבון ופחמימה בחלון הזמנים שלאחר האימון.");
                    wDto.setWaterRecommendation("זכור להחזיר נוזלים ולשתות מים.");
                }
            }
        }
        
        return responseDTO;
    }

    @Override
    public UserProgramDTO getActiveProgramByUserId(Long userId) {
        // שליפה ישירה, מהירה וממוקדת מה-DB בעזרת הפונקציה הממוקדת ב-Repository!
        UserProgram program = userProgramRepository.findByUserIdAndStatus(userId, ProgramStatus.ACTIVE)
                .orElseThrow(() -> new ProgramException("אין לך כרגע תוכנית אימונים פעילה. מלא את השאלון כדי להתחיל!"));
        
        UserProgramDTO responseDTO = mapper.map(program, UserProgramDTO.class);
        
        // התאמת הטיפים התזונתיים לאימונים גם בשליפת ה-Dashboard הרגילה
        List<WorkoutDTO> workoutDTOs = responseDTO.getWorkouts();
        if (workoutDTOs != null) {
            for (WorkoutDTO wDto : workoutDTOs) {
                nutritionTipRepository.findTipForCalories(wDto.getCaloriesBurned()).ifPresent(tip -> {
                    wDto.setFoodRecommendation(tip.getFoodRecommendation());
                    wDto.setWaterRecommendation(tip.getWaterRecommendation());
                });
            }
        }
        
        return responseDTO;
    }
}