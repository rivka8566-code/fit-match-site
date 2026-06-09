package com.fitway.fitmatch.entity;

import com.fitway.fitmatch.entity.enums.ProgramStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user_programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate startDate;
    private int durationWeeks;
    private int daysPerWeekTarget;
    private int totalTargetCalories;
    private int burnedCaloriesInProgram = 0;

    @Enumerated(EnumType.STRING)
    private ProgramStatus status; // ACTIVE, COMPLETED, FUTURE

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_program_workouts", joinColumns = @JoinColumn(name = "program_id"), inverseJoinColumns = @JoinColumn(name = "workout_id"))
    @OrderColumn(name = "workout_order")
    private List<Workout> workouts = new java.util.ArrayList<>();
}