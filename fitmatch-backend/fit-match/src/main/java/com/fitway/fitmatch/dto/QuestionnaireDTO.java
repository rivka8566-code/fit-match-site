package com.fitway.fitmatch.dto;

import com.fitway.fitmatch.entity.enums.DifficultyLevel;
import com.fitway.fitmatch.entity.enums.WorkoutLocation;
import lombok.Data;
import java.util.List;

@Data
public class QuestionnaireDTO {
    private Long userId;
    private DifficultyLevel difficultyLevel;
    private List<WorkoutLocation> preferredLocations;
    private int daysPerWeek;
    private int durationWeeks;
    private List<Long> preferredBodyPartIds;
    private List<Long> availableEquipmentIds;
}
