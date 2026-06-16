package com.fitway.fitmatch.controller;

import com.fitway.fitmatch.service.WorkoutService;
import com.fitway.fitmatch.service.NutritionTipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // התאמה מלאה לשאר הקונטרולרים באתר
public class ChatController {

    private final WorkoutService workoutService;
    private final NutritionTipService nutritionTipService;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String PYTHON_BOT_URL = "http://localhost:5000/api/chat";

    @PostMapping("/message")
public ResponseEntity<Object> sendMessageToBot(@RequestBody Map<String, Object> payload) {
    try {
        System.out.println("=== בדיקת שליפת נתונים עבור הבוט ===");
        
        var allWorkouts = workoutService.getAllWorkouts();
        System.out.println("אימונים שנשלפו מה-Service: " + (allWorkouts != null ? allWorkouts.toString() : "NULL"));

        var allTips = nutritionTipService.getAllTips();
        System.out.println("טיפים שנשלפו מה-Service: " + (allTips != null ? allTips.toString() : "NULL"));
        
        Map<String, Object> enrichedPayload = new HashMap<>(payload);
        enrichedPayload.put("workoutsDB", allWorkouts);
        enrichedPayload.put("tipsDB", allTips);

        // העברת המידע המסונכרן לשרת הפייתון
        Object response = restTemplate.postForObject(PYTHON_BOT_URL, enrichedPayload, Object.class);
        return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "שגיאה בסנכרון הנתונים מול ה-DB: " + e.getMessage()));
        }
    }
}