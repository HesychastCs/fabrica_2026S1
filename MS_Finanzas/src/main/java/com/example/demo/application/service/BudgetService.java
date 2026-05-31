package com.example.demo.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.BudgetRepositoryPort;
import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.application.usecase.AddBudgetUseCase;
import com.example.demo.application.usecase.DeleteBudgetUseCase;
import com.example.demo.application.usecase.GetBudgetUseCase;
import com.example.demo.application.usecase.UpdateBudgetUseCase;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Budget;
import com.example.demo.domain.model.TypeTransaction;

@Service
public class BudgetService implements AddBudgetUseCase, GetBudgetUseCase, UpdateBudgetUseCase, DeleteBudgetUseCase {
    private final BudgetRepositoryPort budgetRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final TitularRepositoryPort titularRepositoryPort;

    public BudgetService(BudgetRepositoryPort budgetRepositoryPort,TransactionRepositoryPort transactionRepositoryPort, TitularRepositoryPort titularRepositoryPort) {
        this.titularRepositoryPort = titularRepositoryPort;
        this.budgetRepositoryPort = budgetRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public Optional<Budget> findById(UUID id) {
        return budgetRepositoryPort.findById(id).map(this::recalculateBudgetAmounts);
    }
    
    @Override
    public List<Budget> findAll() {
        return budgetRepositoryPort.findAll().stream()
            .map(this::recalculateBudgetAmounts)
            .toList();
    }
    @Override
    public Budget addBudget(Budget budget, LocalDate from, LocalDate to) {
        titularRepositoryPort.findById(budget.titular().titularId())
        .orElseThrow(() -> new ResourceNotFoundException("El titular no fue encontrado"));
        BigDecimal gastoAcumulado = transactionRepositoryPort.sumByTitularAndTypeAndDateRange(
                budget.titular().titularId(),
                TypeTransaction.GASTO,
                from,
                to
            );
    
        BigDecimal montoDisponible = budget.montoLimite().subtract(gastoAcumulado);

        Budget budgetToSave = new Budget(
            null,
            budget.montoLimite(),
            Instant.now(),
            from, 
            to,
            gastoAcumulado,
            montoDisponible,
            budget.titular()
        );
        
        return budgetRepositoryPort.save(budgetToSave);
    }

    @Override
    public Budget updateBudget(UUID budgetId, Budget budget) {
        Budget existingBudget = budgetRepositoryPort.findById(budgetId)
        .orElseThrow(() -> new ResourceNotFoundException("El presupuesto no fue encontrado"));
        
        LocalDate fechaInicio = budget.fechaInicio() != null ? budget.fechaInicio() : existingBudget.fechaInicio();
        LocalDate fechaFinal = budget.fechaFinal() != null ? budget.fechaFinal() : existingBudget.fechaFinal();

        BigDecimal gastoAcumulado = transactionRepositoryPort.sumByTitularAndTypeAndDateRange(
        budget.titular().titularId(),
        TypeTransaction.GASTO,
        fechaInicio,
        fechaFinal
        );

        BigDecimal montoDisponible = budget.montoLimite().subtract(gastoAcumulado);

        Budget updatedBudget = new Budget(
            budgetId,
            budget.montoLimite(),
            existingBudget.fechaCreacion(),  // ✅ Use existing creation date
            fechaInicio,
            fechaFinal,
            gastoAcumulado,
            montoDisponible,
            budget.titular()
        );
        
        return budgetRepositoryPort.update(budgetId, updatedBudget);
    }

    @Override
    public void deleteBudget(UUID budgetId) {
        budgetRepositoryPort.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("El presupuesto no fue encontrado"));
        
        budgetRepositoryPort.deleteById(budgetId);
        }

    private Budget recalculateBudgetAmounts(Budget budget) {
        BigDecimal gastoAcumulado = transactionRepositoryPort.sumByTitularAndTypeAndDateRange(
        budget.titular().titularId(),
        TypeTransaction.GASTO,
        budget.fechaInicio(),
        budget.fechaFinal()
        );
        
        BigDecimal montoDisponible = budget.montoLimite().subtract(gastoAcumulado);
        
        return new Budget(
            budget.presupuestoId(),
            budget.montoLimite(),
            budget.fechaCreacion(),
            budget.fechaInicio(),
            budget.fechaFinal(),
            gastoAcumulado,
            montoDisponible,
            budget.titular()
        );
    }
}
