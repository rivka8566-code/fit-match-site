package com.fitway.fitmatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitway.fitmatch.entity.BodyPart;

@Repository
public interface BodyPartRepository extends JpaRepository<BodyPart, Long> {
}
