package com.fitway.fitmatch.repository;

import com.fitway.fitmatch.entity.ProgramWorkoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramWorkoutStatusRepository extends JpaRepository<ProgramWorkoutStatus, Long> {
    List<ProgramWorkoutStatus> findByProgramId(Long programId);
    Optional<ProgramWorkoutStatus> findByProgramIdAndWorkoutId(Long programId, Long workoutId);
    Optional<ProgramWorkoutStatus> findByProgramIdAndSequence(Long programId, int sequence);
}
