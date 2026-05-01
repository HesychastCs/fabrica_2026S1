package com.example.demo.application.usecase;

import com.example.demo.domain.model.SavingGoal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RetrieveSavingGoalUseCase {
    Optional<SavingGoal> findById(UUID id);
    List<SavingGoal> findAll();
}