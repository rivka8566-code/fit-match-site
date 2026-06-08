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

    // 1. קבלת נתוני השאלון ובניית תוכנית חדשה
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

    // 2. שליפת התוכנית הפעילה הנוכחית עבור מסך ה-Dashboard של המשתמש
    @GetMapping("/active/{userId}")
    public ResponseEntity<UserProgramDTO> getActiveProgram(@PathVariable Long userId) {
        UserProgramDTO activeProgram = userProgramService.getActiveProgramByUserId(userId);
        return ResponseEntity.ok(activeProgram);
    }

    // 3. פיצ'ר ה-Shuffle: החלפת אימון ספציפי בתוכנית באימון דומה אחר
    @PostMapping("/{programId}/swap/{workoutId}")
    public ResponseEntity<WorkoutDTO> swapWorkout(
            @PathVariable Long programId,
            @PathVariable Long workoutId) {
        WorkoutDTO newWorkout = workoutService.swapWorkoutInProgram(programId, workoutId);
        return ResponseEntity.ok(newWorkout);
    }

    // 4. שליפת כל התוכניות שהיו למשתמש אי פעם (היסטוריית תוכניות)
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<UserProgramDTO>> getAllUserPrograms(@PathVariable Long userId) {
        List<UserProgramDTO> allPrograms = userProgramService.getAllProgramsByUserId(userId);
        return ResponseEntity.ok(allPrograms);
    }

    // 5. הפעלת תוכנית עתידית ידנית (כשאין תוכנית פעילה)
    @PostMapping("/{programId}/activate")
    public ResponseEntity<UserProgramDTO> activateProgram(@PathVariable Long programId) {
        UserProgramDTO activated = userProgramService.activateFutureProgram(programId);
        return ResponseEntity.ok(activated);
    }
}