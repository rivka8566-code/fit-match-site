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
    private User user; // המשתמש אליו משויכת התוכנית

    private LocalDate startDate; // תאריך התחלה שהמשתמש בחר
    private int durationWeeks;
    private int daysPerWeekTarget;
    private int totalTargetCalories;

    @Enumerated(EnumType.STRING)
    private ProgramStatus status; // ACTIVE, COMPLETED, FUTURE

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "program_workouts",
        joinColumns = @JoinColumn(name = "program_id"),
        inverseJoinColumns = @JoinColumn(name = "workout_id")
    )
    private List<Workout> workouts;
}