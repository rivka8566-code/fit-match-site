package com.fitway.fitmatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitway.fitmatch.entity.Equipment;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}
