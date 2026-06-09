package com.fitway.fitmatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "program_workout_status",
       uniqueConstraints = @UniqueConstraint(columnNames = {"program_id", "workout_id", "sequence"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramWorkoutStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Column(name = "workout_id", nullable = false)
    private Long workoutId;

    @Column(nullable = false)
    private int sequence; // מיקום האימון בסדר השבועי (פותר את הבעיה!)

    @Column(nullable = false)
    private boolean completed = false;
}