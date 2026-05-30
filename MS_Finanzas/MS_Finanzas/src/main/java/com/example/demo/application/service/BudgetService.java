package com.example.demo.application.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.BudgetRepositoryPort;
import com.example.demo.application.usecase.AddBudgetUseCase;
import com.example.demo.application.usecase.DeleteBudgetUseCase;
import com.example.demo.application.usecase.GetBudgetUseCase;
import com.example.demo.application.usecase.UpdateBudgetUseCase;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Budget;

@Service
public class BudgetService implements AddBudgetUseCase, GetBudgetUseCase, UpdateBudgetUseCase, DeleteBudgetUseCase {
    private final BudgetRepositoryPort budgetRepositoryPort;
    
    public BudgetService(BudgetRepositoryPort budgetRepositoryPort) {
        this.budgetRepositoryPort = budgetRepositoryPort;
    }

    @Override
    public Optional<Budget> findById(UUID id) {
        return budgetRepositoryPort.findById(id);
    }
    
    @Override
    public List<Budget> findAll() {
        return budgetRepositoryPort.findAll();
    }
    @Override
    public Budget addBudget(Budget budget) {
        return budgetRepositoryPort.save(budget);
    }

    @Override
    public Budget updateBudget(UUID budgetId, Budget budget) {
        budgetRepositoryPort.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("El presupuesto no fue encontrado"));
        
        return budgetRepositoryPort.update(budgetId, budget);
    }

    @Override
    public void deleteBudget(UUID budgetId) {
        budgetRepositoryPort.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("El presupuesto no fue encontrado"));
        
        budgetRepositoryPort.deleteById(budgetId);
    }
}
