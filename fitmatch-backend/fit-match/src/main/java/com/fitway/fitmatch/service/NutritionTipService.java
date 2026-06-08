package com.fitway.fitmatch.service;

import java.util.List;

import com.fitway.fitmatch.dto.NutritionTipDTO;

public interface NutritionTipService {
    void add(NutritionTipDTO tipDto);
    List<NutritionTipDTO> getAllTips(); // חדש
    void deleteById(Long id); // חדש
}