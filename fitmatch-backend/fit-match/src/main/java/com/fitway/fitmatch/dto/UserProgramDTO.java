package com.fitway.fitmatch.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserProgramDTO {
    private Long id;
    private LocalDate startDate;
    private int durationWeeks;
    private int daysPerWeekTarget;
    private int totalTargetCalories;
    private List<WorkoutDTO> workouts; // כל אימון בפנים כבר מכיל את הטיפ שלו
}