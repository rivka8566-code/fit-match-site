package com.fitway.fitmatch.dto;

import com.fitway.fitmatch.entity.enums.DifficultyLevel;
import com.fitway.fitmatch.entity.enums.WorkoutLocation;
import lombok.Data;
import java.util.List;

@Data
public class WorkoutCreateDTO {
    private String title;
    private String description;
    private String youtubeUrl;
    private int durationMinutes;
    private int caloriesBurned;
    private DifficultyLevel difficultyLevel;
    private WorkoutLocation location;
    private List<Long> equipmentIds;
    private List<Long> bodyPartIds;
}
