package com.example.demo.application.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;

public interface TransactionRepositoryPort {

    List<Transaction> findAll(TransactionListFilter filter);

    Optional<Transaction> findById(UUID id);

    Transaction save(Transaction transaccion);

    void deleteById(UUID id);

    boolean existsByCategoryId(UUID categoryId);

    BigDecimal sumByTitularAndType(UUID titularId, TypeTransaction type);
    
    BigDecimal sumByTitularAndTypeAndMonth(UUID titularId, TypeTransaction type, Integer mes, Integer anho);

    BigDecimal sumByTitularAndTypeAndDateRange(UUID titularId, TypeTransaction type, LocalDate fechaInicio, LocalDate fechaFinal);

    List<Transaction> findFiltered(
        TypeTransaction tipo,
        UUID categoriaId,
        UUID titularId,
        LocalDate desde,
        LocalDate hasta
    );
}
