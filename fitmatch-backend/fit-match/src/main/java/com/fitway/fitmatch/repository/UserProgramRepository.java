package com.fitway.fitmatch.repository;

import com.fitway.fitmatch.entity.UserProgram;
import com.fitway.fitmatch.entity.enums.ProgramStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserProgramRepository extends JpaRepository<UserProgram, Long> {
    Optional<UserProgram> findByUserIdAndStatus(Long userId, ProgramStatus status);
}