package com.fitway.fitmatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nutrition_tips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // המערכת תתאים המלצה לפי טווח הקלוריות שנשרפו באימון
    private int minCaloriesThreshold;
    private int maxCaloriesThreshold;

    @Column(length = 1000, nullable = false)
    private String foodRecommendation; // המלצת אוכל (חלבונים, פחמימות)

    @Column(length = 500, nullable = false)
    private String waterRecommendation; // משימת שתיית מים מותאמת
}