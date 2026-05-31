package com.example.demo.infra.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.application.repository.BudgetRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Budget;
import com.example.demo.infra.mapper.BudgetEntityMapper;
import com.example.demo.infra.persistence.entity.BudgetEntity;

@Component
public class JpaBudgetRepositoryAdapter implements BudgetRepositoryPort {
    private final JpaBudgetRepository jpaBudgetRepository;
    private final BudgetEntityMapper budgetEntityMapper;

    public JpaBudgetRepositoryAdapter(JpaBudgetRepository jpaBudgetRepository, BudgetEntityMapper budgetEntityMapper) {
        this.jpaBudgetRepository = jpaBudgetRepository;
        this.budgetEntityMapper = budgetEntityMapper;
    }

    @Override
    public List<Budget> findAll() {
        return jpaBudgetRepository.findAll().stream().map(budgetEntityMapper::toDomain).toList();
    }

    @Override
    public Optional<Budget> findById(UUID budgetId) {
        return jpaBudgetRepository.findById(budgetId)
            .map(budgetEntityMapper::toDomain);
    }

    @Override
    public Budget save(Budget budget) {
        BudgetEntity budgetEntity = budgetEntityMapper.toEntity(budget);
        jpaBudgetRepository.save(budgetEntity);
        return budget;
    }

    @Override
    public Budget update(UUID budgetId, Budget budget) {
        BudgetEntity existingBudgetEntity = jpaBudgetRepository.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("El presupuesto no fue encontrado"));
        
        existingBudgetEntity.setMontoLimite(budget.montoLimite());
        existingBudgetEntity.setFechaInicio(budget.fechaInicio());
        existingBudgetEntity.setFechaFinal(budget.fechaFinal());

        jpaBudgetRepository.save(existingBudgetEntity);
        return budget;
    }

    @Override
    public void deleteById(UUID budgetId) {
        jpaBudgetRepository.deleteById(budgetId);
    }

    @Override
    public List<Budget> findByTitularAndDateRange(UUID titularId, LocalDate fecha) {
        return jpaBudgetRepository.findByTitularAndDateRange(titularId, fecha)
            .stream()
            .map(budgetEntityMapper::toDomain)
            .toList();
    }

    
}
