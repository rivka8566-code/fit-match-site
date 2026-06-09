package com.fitway.fitmatch.controller;

import com.fitway.fitmatch.dto.QuestionnaireDTO;
import com.fitway.fitmatch.dto.UserProgramDTO;
import com.fitway.fitmatch.dto.WorkoutDTO;
import com.fitway.fitmatch.service.ProgramWorkoutStatusService;
import com.fitway.fitmatch.service.UserProgramService;
import com.fitway.fitmatch.service.WorkoutService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProgramController {

    private final UserProgramService userProgramService;
    private final ProgramWorkoutStatusService programWorkoutStatusService;
    private final WorkoutService workoutService;

    @PostMapping("/create")
    public ResponseEntity<UserProgramDTO> createProgram(@RequestBody QuestionnaireDTO questionnaire) {
        UserProgramDTO newProgram = userProgramService.createProgramFromQuestionnaire(questionnaire);
        return ResponseEntity.ok(newProgram);
    }

    @PostMapping("/{programId}/complete/{workoutId}")
    public ResponseEntity<UserProgramDTO> completeWorkout(
            @PathVariable Long programId,
            @PathVariable Long workoutId,
            @RequestParam(required = false) Integer sequence) {
        UserProgramDTO program = programWorkoutStatusService.markWorkoutCompleted(programId, workoutId, sequence);
        return ResponseEntity.ok(program);
    }

    @GetMapping("/active/{userId}")
    public ResponseEntity<UserProgramDTO> getActiveProgram(@PathVariable Long userId) {
        UserProgramDTO activeProgram = userProgramService.getActiveProgramByUserId(userId);
        return ResponseEntity.ok(activeProgram);
    }

    @PostMapping("/{programId}/swap/{workoutId}")
    public ResponseEntity<WorkoutDTO> swapWorkout(
            @PathVariable Long programId,
            @PathVariable Long workoutId) {
        WorkoutDTO newWorkout = workoutService.swapWorkoutInProgram(programId, workoutId);
        return ResponseEntity.ok(newWorkout);
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<UserProgramDTO>> getAllUserPrograms(@PathVariable Long userId) {
        List<UserProgramDTO> allPrograms = userProgramService.getAllProgramsByUserId(userId);
        return ResponseEntity.ok(allPrograms);
    }

    @PostMapping("/{programId}/activate")
    public ResponseEntity<UserProgramDTO> activateProgram(@PathVariable Long programId) {
        UserProgramDTO activated = userProgramService.activateFutureProgram(programId);
        return ResponseEntity.ok(activated);
    }
}