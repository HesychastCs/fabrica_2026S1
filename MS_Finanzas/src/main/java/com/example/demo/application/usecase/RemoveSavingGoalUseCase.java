package com.example.demo.application.usecase;

import java.util.UUID;

public interface RemoveSavingGoalUseCase {
    void deleteSavingGoalById(UUID goalId);
}