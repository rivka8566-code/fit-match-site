package com.fitway.fitmatch.controller;

import com.fitway.fitmatch.exception.UserAuthException;
import com.fitway.fitmatch.exception.ProgramException;
import com.fitway.fitmatch.exception.WorkoutException; // ה-Import לשגיאה החדשה
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(UserAuthException.class)
    public ResponseEntity<String> handleAuthException(UserAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(ProgramException.class)
    public ResponseEntity<String> handleProgramException(ProgramException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // הנדלר החדש שתופס את שגיאות האימונים וה-Shuffle המשעשע!
    @ExceptionHandler(WorkoutException.class)
    public ResponseEntity<String> handleWorkoutException(WorkoutException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("אופסס... משהו קטן השתבש בשרת, הצוות הטכני שלנו כבר מטפל בזה!");
    }
}