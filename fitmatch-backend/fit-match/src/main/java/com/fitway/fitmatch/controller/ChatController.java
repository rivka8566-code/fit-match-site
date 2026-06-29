package com.fitway.fitmatch.controller;

import com.fitway.fitmatch.service.WorkoutService;
import com.fitway.fitmatch.service.NutritionTipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final WorkoutService workoutService;
    private final NutritionTipService nutritionTipService;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String PYTHON_BOT_URL = "http://localhost:5000/api/chat";

    @PostMapping("/message")
    public ResponseEntity<Object> sendMessageToBot(@RequestBody Map<String, Object> payload) {
        try {
            // העברת ההודעה וההיסטוריה ישירות לפייתון ללא שליפה מיותרת מה-DB
            Object response = restTemplate.postForObject(PYTHON_BOT_URL, payload, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "שגיאה בתקשורת מול שרת הבוט: " + e.getMessage()));
        }
    }

    // נקודת קצה לשליפת אימונים לפי דרישה עבור שירות הפייתון
    @GetMapping("/workouts")
    public ResponseEntity<List<?>> getAllWorkoutsForBot() {
        try {
            return ResponseEntity.ok(workoutService.getAllWorkouts());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // נקודת קצה לשליפת טיפי תזונה לפי דרישה עבור שירות הפייתון
    @GetMapping("/nutrition-tips")
    public ResponseEntity<List<?>> getAllTipsForBot() {
        try {
            return ResponseEntity.ok(nutritionTipService.getAllTips());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}