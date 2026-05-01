package com.example.demo.application.usecase;

import com.example.demo.domain.model.SavingGoal;

import java.util.UUID;

public interface UpdateSavingGoalUseCase {
    SavingGoal updateSavingGoal(UUID goalId, SavingGoal savingGoal);
}