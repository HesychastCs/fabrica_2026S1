package com.example.demo.application.repository;

import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.SavingGoal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavingGoalRepositoryPort {
    SavingGoal save(SavingGoal savingGoal);
    Optional<SavingGoal> findById(UUID id);
    List<SavingGoal> findAll();
    SavingGoal update(UUID id, SavingGoal savingGoal);
    void deleteById(UUID id);
    boolean existsByNombre(String nombre);
    SavingGoal updateAvance(UUID goalId, int nuevoAvance, GoalStatus nuevoEstado);
}