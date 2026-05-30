package com.example.demo.application.usecase;

import java.util.UUID;

import com.example.demo.domain.model.Budget;

public interface UpdateBudgetUseCase {
    Budget updateBudget(UUID budgetId, Budget budget);
}
