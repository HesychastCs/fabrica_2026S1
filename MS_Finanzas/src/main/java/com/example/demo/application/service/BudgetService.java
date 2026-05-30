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
import com.example.demo.domain.model.Transaction;
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
        return budgetRepositoryPort.findById(id);
    }
    
    @Override
    public List<Budget> findAll() {
        return budgetRepositoryPort.findAll();
    }
    @Override
    public Budget addBudget(Budget budget, LocalDate from, LocalDate to) {
        titularRepositoryPort.findById(budget.titular().titularId())
        .orElseThrow(() -> new ResourceNotFoundException("El titular no fue encontrado"));

        List<Transaction> transactions = transactionRepositoryPort.findFiltered(
            TypeTransaction.GASTO,
            null,
            budget.titular().titularId(),
            from,
            to
        );
        BigDecimal gastoAcumulado = transactions.stream()
            .map(Transaction::monto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal montoDisponible = budget.montoLimite().subtract(gastoAcumulado);

        budget = new Budget(
            null,
            budget.montoLimite(),
            Instant.now(),
            budget.fechaInicio(),
            budget.fechaFinal(),
            gastoAcumulado,
            montoDisponible,
            budget.titular()
        );
        return budgetRepositoryPort.save(budget);
    }

    @Override
    public Budget updateBudget(UUID budgetId, Budget budget) {
        budgetRepositoryPort.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("El presupuesto no fue encontrado"));
        
        List<Transaction> transactions = transactionRepositoryPort.findFiltered(
        TypeTransaction.GASTO,
        null,
        budget.titular().titularId(),
        budget.fechaInicio(),
        budget.fechaFinal()
    );
    BigDecimal gastoAcumulado = transactions.stream()
        .map(Transaction::monto)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    BigDecimal montoDisponible = budget.montoLimite().subtract(gastoAcumulado);

    Budget updatedBudget = new Budget(
        budgetId,
        budget.montoLimite(),
        budget.fechaCreacion(),
        budget.fechaInicio(),
        budget.fechaFinal(),
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
}
