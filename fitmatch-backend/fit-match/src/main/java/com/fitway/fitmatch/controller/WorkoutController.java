package com.fitway.fitmatch.controller;

import com.fitway.fitmatch.dto.WorkoutDTO;
import com.fitway.fitmatch.entity.enums.DifficultyLevel;
import com.fitway.fitmatch.entity.enums.WorkoutLocation;
import com.fitway.fitmatch.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkoutController {

    private final WorkoutService workoutService;

    // 1. שער פתוח: קבלת כל האימונים בעולם לספריית האימונים (כמו החדרים ב-THM)
    @GetMapping
    public ResponseEntity<List<WorkoutDTO>> getAllWorkouts() {
        List<WorkoutDTO> workouts = workoutService.getAllWorkouts();
        return ResponseEntity.ok(workouts);
    }

    // 2. חיפוש וסינון מתקדם: סנן לפי רמת קושי ומיקום האימון
    @GetMapping("/search")
    public ResponseEntity<List<WorkoutDTO>> searchWorkouts(
            @RequestParam(required = false) DifficultyLevel difficulty,
            @RequestParam(required = false) WorkoutLocation location) {
        List<WorkoutDTO> filteredWorkouts = workoutService.getFilteredWorkouts(difficulty, location);
        return ResponseEntity.ok(filteredWorkouts);
    }
}