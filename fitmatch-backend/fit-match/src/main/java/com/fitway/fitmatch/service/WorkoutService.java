package com.fitway.fitmatch.service;

import com.fitway.fitmatch.dto.WorkoutDTO;
import com.fitway.fitmatch.entity.enums.DifficultyLevel;
import com.fitway.fitmatch.entity.enums.WorkoutLocation;

import java.util.List;

public interface WorkoutService {
    WorkoutDTO swapWorkoutInProgram(Long programId, Long currentWorkoutId);

    List<WorkoutDTO> getAllWorkouts();

    List<WorkoutDTO> getFilteredWorkouts(DifficultyLevel difficulty, WorkoutLocation location);
}