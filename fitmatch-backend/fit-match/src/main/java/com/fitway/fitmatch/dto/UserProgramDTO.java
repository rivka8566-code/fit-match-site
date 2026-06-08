package com.fitway.fitmatch.dto;

import com.fitway.fitmatch.entity.enums.ProgramStatus;
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
    private int burnedCaloriesInProgram;
    private ProgramStatus status;
    private List<WorkoutDTO> workouts;
}
