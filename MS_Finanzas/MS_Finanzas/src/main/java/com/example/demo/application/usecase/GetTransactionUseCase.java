package com.example.demo.application.usecase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.domain.model.Transaction;

public interface GetTransactionUseCase {

    List<Transaction> findAll(TransactionListFilter filter);

    Optional<Transaction> findById(UUID id);
}
