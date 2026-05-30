package com.example.demo.infra.persistence.repository;

import com.example.demo.infra.persistence.entity.SavingGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaSavingGoalRepository extends JpaRepository<SavingGoalEntity, UUID> {
    boolean existsByNombre(String nombre);
}