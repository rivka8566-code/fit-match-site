package com.fitway.fitmatch.service;

import com.fitway.fitmatch.dto.WorkoutDTO;
import java.util.List;

public interface WorkoutService {
    WorkoutDTO swapWorkoutInProgram(Long programId, Long currentWorkoutId);
    List<WorkoutDTO> getAllWorkouts();
}