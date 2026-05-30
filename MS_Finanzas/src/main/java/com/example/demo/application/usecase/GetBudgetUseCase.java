package com.example.demo.application.usecase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Budget;

public interface GetBudgetUseCase {
    Optional<Budget> findById(UUID budgetId);
    List<Budget> findAll();
}
