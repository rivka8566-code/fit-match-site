package com.fitway.fitmatch.service;

import com.fitway.fitmatch.dto.UserProgramDTO;

public interface ProgramWorkoutStatusService {
    UserProgramDTO markWorkoutCompleted(Long programId, Long workoutId, Integer sequence);
}
