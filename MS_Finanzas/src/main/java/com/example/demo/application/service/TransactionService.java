package com.example.demo.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.repository.SavingGoalRepositoryPort;
import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.application.usecase.CreateTransactionUseCase;
import com.example.demo.application.usecase.DeleteTransactionUseCase;
import com.example.demo.application.usecase.GetTransactionUseCase;
import com.example.demo.application.usecase.UpdateTransactionUseCase;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.EmptyCategoryConstants;
import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;

@Service
public class TransactionService implements
    CreateTransactionUseCase,
    GetTransactionUseCase,
    UpdateTransactionUseCase,
    DeleteTransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final TitularRepositoryPort titularRepositoryPort;
    private final SavingGoalRepositoryPort savingGoalRepositoryPort;

    public TransactionService(
        TransactionRepositoryPort transactionRepositoryPort,
        CategoryRepositoryPort categoryRepositoryPort,
        TitularRepositoryPort titularRepositoryPort,
        SavingGoalRepositoryPort savingGoalRepositoryPort
    ) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.titularRepositoryPort = titularRepositoryPort;
        this.savingGoalRepositoryPort = savingGoalRepositoryPort;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Transaction prepared = prepareForPersist(transaction, null);

        if (prepared.tipo() == TypeTransaction.APORTE_META ||
                prepared.tipo() == TypeTransaction.RETIRO_META) {

            if (prepared.goalId() == null)
                throw new IllegalArgumentException(
                        "Para aporte_meta o retiro_meta debes indicar el goalId");

            SavingGoal goal = savingGoalRepositoryPort.findById(prepared.goalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Meta no encontrada con id: " + prepared.goalId()));

            if (goal.estado() == GoalStatus.COMPLETADA || goal.estado() == GoalStatus.VENCIDA) {
                throw new IllegalArgumentException(
                        "La meta con estado '" + goal.estado().name() + "' no acepta nuevas transacciones");
            }

            double montoTransaccion = prepared.monto().doubleValue();
            int avanceActual = goal.avance() != null ? goal.avance().intValue() : 0;
            double montoObjetivo = goal.montoObjetivo().doubleValue();
            double montoAcumuladoActual = (montoObjetivo * avanceActual) / 100.0;

            double nuevoMontoAcumulado;
            
            if (prepared.tipo() == TypeTransaction.APORTE_META) {
                // Validación: El aporte no puede superar el monto restante para completar la meta
                double montoRestante = montoObjetivo - montoAcumuladoActual;
                if (montoTransaccion > montoRestante) {
                    throw new IllegalArgumentException(
                            "El aporte de $" + montoTransaccion + 
                            " excede el monto restante para completar la meta: $" + montoRestante +
                            ". Puedes aportar como máximo $" + montoRestante);
                }
                nuevoMontoAcumulado = montoAcumuladoActual + montoTransaccion;
            } else {
                // Validación: El retiro no puede superar el monto actual ahorrado
                if (montoTransaccion > montoAcumuladoActual) {
                    throw new IllegalArgumentException(
                            "El retiro de $" + montoTransaccion + 
                            " excede el monto actual ahorrado: $" + montoAcumuladoActual +
                            ". Puedes retirar como máximo $" + montoAcumuladoActual);
                }
                nuevoMontoAcumulado = montoAcumuladoActual - montoTransaccion;
                if (nuevoMontoAcumulado < 0) nuevoMontoAcumulado = 0;
            }

            int nuevoAvance = (int) Math.min(100, (int) Math.round((nuevoMontoAcumulado / montoObjetivo) * 100));
            GoalStatus nuevoEstado = nuevoAvance >= 100 ? GoalStatus.COMPLETADA : GoalStatus.EN_PROGRESO;

            savingGoalRepositoryPort.updateAvance(goal.goalId(), nuevoAvance, nuevoEstado);
        }

        return transactionRepositoryPort.save(prepared);
    }

    @Override
    public List<Transaction> findAll(TransactionListFilter filter) {
        return transactionRepositoryPort.findAll(filter);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return transactionRepositoryPort.findById(id);
    }

    @Override
    public Transaction updateTransaction(UUID id, Transaction transaction) {
        transactionRepositoryPort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
        Transaction prepared = prepareForPersist(transaction, id);
        return transactionRepositoryPort.save(prepared);
    }

    @Override
    public void deleteTransaction(UUID id) {
        transactionRepositoryPort.deleteById(id);
    }

    private Transaction prepareForPersist(Transaction partial, UUID transactionId) {
        Titular titular = titularRepositoryPort.findById(partial.titular().titularId())
            .orElseThrow(() -> new ResourceNotFoundException("Titular no encontrado"));

        Category category = resolveCategory(partial.categoria());

        LocalDate fecha = partial.fecha() != null ? partial.fecha() : LocalDate.now();

        return new Transaction(
            transactionId,
            partial.nombre(),
            partial.descripcion(),
            partial.monto(),
            partial.tipo(),
            fecha,
            category,
            titular,
            partial.goalId()
        );
    }

    private Category resolveCategory(Category categoriaPartial) {
        if (categoriaPartial != null && categoriaPartial.categoriaId() != null) {
            return categoryRepositoryPort.findById(categoriaPartial.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        }
        return categoryRepositoryPort.findByNombreIgnoreCase(EmptyCategoryConstants.NAME)
            .orElseGet(() -> categoryRepositoryPort.save(
                new Category(null, EmptyCategoryConstants.NAME, null)
            ));
    }
}