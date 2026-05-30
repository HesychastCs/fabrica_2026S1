package com.example.demo.application.usecase;

import java.time.LocalDate;

import com.example.demo.domain.model.Budget;

public interface AddBudgetUseCase {
    Budget addBudget(Budget budget, LocalDate from, LocalDate to);
}
