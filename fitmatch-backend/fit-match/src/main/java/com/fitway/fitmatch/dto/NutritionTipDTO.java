package com.fitway.fitmatch.dto;

import lombok.Data;

@Data
public class NutritionTipDTO {
    private Long id;
    private String foodRecommendation;
    private String waterRecommendation;
    private int minCaloriesThreshold;
    private int maxCaloriesThreshold;
}