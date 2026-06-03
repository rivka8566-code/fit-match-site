package com.fitway.fitmatch.service;

import com.fitway.fitmatch.dto.NutritionTipDTO;
import com.fitway.fitmatch.entity.NutritionTip;
import com.fitway.fitmatch.exception.ProgramException;
import com.fitway.fitmatch.repository.NutritionTipRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NutritionTipServiceImpl implements NutritionTipService {

    private final NutritionTipRepository nutritionTipRepository;
    private final ModelMapper mapper;

    @Override
    public void add(NutritionTipDTO tipDto) {
        // בדיקת הגנה קטנה: שלא יכניסו טווח קלוריות הפוך (למשל מינימום גדול ממקסימום)
        if (tipDto.getMinCaloriesThreshold() > tipDto.getMaxCaloriesThreshold()) {
            throw new ProgramException("שגיאה: טווח הקלוריות המינימלי אינו יכול להיות גדול מהטווח המקסימלי!");
        }

        // המרה מ-DTO לישות NutritionTip ושמירה ב-DB (בדיוק כמו אצל המורה)
        nutritionTipRepository.save(mapper.map(tipDto, NutritionTip.class));
    }
}