package com.fitway.fitmatch.dto;

import com.fitway.fitmatch.entity.enums.DifficultyLevel;
import com.fitway.fitmatch.entity.enums.WorkoutLocation;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class QuestionnaireDTO {
    private Long userId;
    private DifficultyLevel difficultyLevel; // מתחיל, בינוני, אלוף
    private WorkoutLocation preferredLocation; // בית, בחוץ, חדר כושר
    private int daysPerWeek; // כמה ימים בשבוע רוצה להתאמן
    private int durationWeeks; // לכמה שבועות התוכנית
    private int weeklyCaloriesGoal; // יעד קלוריות שבועי שהציב לעצמו
    private LocalDate preferredStartDate; // מתי הוא רוצה להתחיל את התוכנית
    
    private List<Long> preferredBodyPartIds; // רשימת מזהי חלקי הגוף לעבודה (בטן, גב וכו')
    private List<Long> availableEquipmentIds; // רשימת מזהי הציוד שיש לו בבית
}