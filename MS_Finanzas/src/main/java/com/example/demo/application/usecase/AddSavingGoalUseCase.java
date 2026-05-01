package com.example.demo.application.usecase;

import com.example.demo.domain.model.SavingGoal;

public interface AddSavingGoalUseCase {
    SavingGoal addSavingGoal(SavingGoal savingGoal);
}