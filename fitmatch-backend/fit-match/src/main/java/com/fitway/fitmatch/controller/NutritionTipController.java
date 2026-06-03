package com.fitway.fitmatch.controller;

import com.fitway.fitmatch.dto.NutritionTipDTO;
import com.fitway.fitmatch.service.NutritionTipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutrition-tips")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // מאפשר ל-React לפנות ל-API
public class NutritionTipController {

    private final NutritionTipService nutritionTipService;

    // נקודת הקצה שמאפשרת למנהל להוסיף חוק תזונה ושתייה חדש
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody NutritionTipDTO tipDto) {
        nutritionTipService.add(tipDto);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // החזרת סטטוס 211 (נוצר בהצלחה)
    }
}