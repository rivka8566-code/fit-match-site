package com.fitway.fitmatch.entity;

import java.util.List;

import com.fitway.fitmatch.entity.enums.DifficultyLevel;
import com.fitway.fitmatch.entity.enums.WorkoutLocation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String youtubeUrl;

    private int durationMinutes; // אורך הסרטון בדקות
    private int caloriesBurned; // כמה קלוריות התרגיל שורף

    @Enumerated(EnumType.STRING) // מאחסן את הערך של הקושי כטקסט במסד הנתונים
    private DifficultyLevel difficultyLevel; // מתחילים, בינוני, אלוף

    @Enumerated(EnumType.STRING)
    private WorkoutLocation location; // בית, בחוץ, חדר כושר

    // בתוך מחלקת Workout, במקום ה-Strings הפשוטים, נגדיר רשימות:

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "workout_equipment", joinColumns = @JoinColumn(name = "workout_id"), inverseJoinColumns = @JoinColumn(name = "equipment_id"))
    private List<Equipment> requiredEquipment = new java.util.ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "workout_body_parts", joinColumns = @JoinColumn(name = "workout_id"), inverseJoinColumns = @JoinColumn(name = "body_part_id"))
    private List<BodyPart> targetBodyParts = new java.util.ArrayList<>();
}
