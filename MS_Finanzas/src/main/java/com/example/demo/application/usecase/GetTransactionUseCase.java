package com.example.demo.application.usecase;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;

public interface GetTransactionUseCase {

    List<Transaction> findAll(TransactionListFilter filter);

    Optional<Transaction> findById(UUID id);

    List<Transaction> findFiltered(
        TypeTransaction tipo,
        UUID categoriaId,
        UUID titularId,
        LocalDate desde,
        LocalDate hasta
    );
}
