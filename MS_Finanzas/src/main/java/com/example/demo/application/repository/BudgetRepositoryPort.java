package com.example.demo.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Budget;

public interface BudgetRepositoryPort {
    List<Budget> findAll();
    Optional<Budget> findById(UUID budgetId);
    Budget save(Budget budget);
    Budget update(UUID budgetId, Budget budget);
    void deleteById(UUID budgetId);
}
