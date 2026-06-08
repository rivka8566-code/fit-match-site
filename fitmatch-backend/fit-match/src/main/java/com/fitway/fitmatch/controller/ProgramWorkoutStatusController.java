package com.fitway.fitmatch.controller;

import com.fitway.fitmatch.dto.UserProgramDTO;
import com.fitway.fitmatch.service.ProgramWorkoutStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Removed REST mapping to avoid duplicate API path with UserProgramController.
@RequiredArgsConstructor
public class ProgramWorkoutStatusController {

    private final ProgramWorkoutStatusService programWorkoutStatusService;

    // סימון אימון כבוצע בתוכנית - מחזיר את ה-DTO המעודכן
    public ResponseEntity<UserProgramDTO> completeWorkout(
            Long programId,
            Long workoutId,
            Integer sequence) {
        UserProgramDTO updated = programWorkoutStatusService.markWorkoutCompleted(programId, workoutId, sequence);
        return ResponseEntity.ok(updated);
    }
}
