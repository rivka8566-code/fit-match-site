package com.fitway.fitmatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fitway.fitmatch.entity.Workout;
import com.fitway.fitmatch.entity.enums.DifficultyLevel;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    
    // שאילתה שמחפשת אימונים חלופיים לפי ה-ID של חלק הגוף, באותה רמת קושי, וללא האימון הנוכחי
    @Query("SELECT w FROM Workout w JOIN w.targetBodyParts bp WHERE w.difficultyLevel = :level " +
           "AND bp.id = :bodyPartId AND w.id != :currentId")
    List<Workout> findAlternatives(
            @Param("level") DifficultyLevel level,
            @Param("bodyPartId") Long bodyPartId,
            @Param("currentId") Long currentId
    );
}
