package com.fitway.fitmatch.service;

import com.fitway.fitmatch.dto.NutritionTipDTO;
import com.fitway.fitmatch.entity.NutritionTip;
import com.fitway.fitmatch.exception.NutritionTipException;
import com.fitway.fitmatch.repository.NutritionTipRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
            throw new NutritionTipException("שגיאה: טווח הקלוריות המינימלי אינו יכול להיות גדול מהטווח המקסימלי!");
        }

        nutritionTipRepository.save(mapper.map(tipDto, NutritionTip.class));
    }

    @Override
    public List<NutritionTipDTO> getAllTips() {
        return nutritionTipRepository.findAll().stream()
                .map(tip -> mapper.map(tip, NutritionTipDTO.class))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        if (!nutritionTipRepository.existsById(id)) {
            throw new NutritionTipException("Tip not found with id: " + id); 
        }
        nutritionTipRepository.deleteById(id);
    }
}