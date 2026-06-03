package com.fitway.fitmatch.repository;

import com.fitway.fitmatch.entity.NutritionTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NutritionTipRepository extends JpaRepository<NutritionTip, Long> {
    
    // שאילתת SQL חכמה (JPQL) שמוצאת את ההמלצה המתאימה ביותר לפי כמות הקלוריות שנשרפו באימון
    @Query("SELECT n FROM NutritionTip n WHERE :calories BETWEEN n.minCaloriesThreshold AND n.maxCaloriesThreshold")
    Optional<NutritionTip> findTipForCalories(@Param("calories") int calories);
}