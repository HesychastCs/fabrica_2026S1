package com.example.demo.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.application.usecase.CreateTransactionUseCase;
import com.example.demo.application.usecase.DeleteTransactionUseCase;
import com.example.demo.application.usecase.GetTransactionUseCase;
import com.example.demo.application.usecase.UpdateTransactionUseCase;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.EmptyCategoryConstants;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;

@Service
public class TransactionService implements
    CreateTransactionUseCase,
    GetTransactionUseCase,
    UpdateTransactionUseCase,
    DeleteTransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final TitularRepositoryPort titularRepositoryPort;

    public TransactionService(
        TransactionRepositoryPort transactionRepositoryPort,
        CategoryRepositoryPort categoryRepositoryPort,
        TitularRepositoryPort titularRepositoryPort
    ) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.titularRepositoryPort = titularRepositoryPort;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Transaction prepared = prepareForPersist(transaction, null);
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
            titular
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
