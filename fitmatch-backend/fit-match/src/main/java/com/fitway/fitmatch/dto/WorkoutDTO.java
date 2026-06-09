package com.fitway.fitmatch.dto;

import com.fitway.fitmatch.entity.enums.DifficultyLevel;
import com.fitway.fitmatch.entity.enums.WorkoutLocation;
import lombok.Data;
import java.util.List;

@Data
public class WorkoutDTO {
    private Long id;
    private String title;
    private String description;
    private String youtubeUrl;
    private int durationMinutes;
    private int caloriesBurned;
    private DifficultyLevel difficultyLevel;
    private WorkoutLocation location;
    private List<EquipmentDTO> requiredEquipment;
    private List<BodyPartDTO> targetBodyParts;
    private String foodRecommendation; //טיפ תזונה לפני או אחרי האימון
    private String waterRecommendation;//טיפ שתייה לפני או אחרי האימון
    private boolean completed; // האם המשתמש סיים אימון זה בתוכנית הנוכחית
}
