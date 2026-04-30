package com.example.demo.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.domain.model.Transaction;

public interface TransactionRepositoryPort {

    List<Transaction> findAll(TransactionListFilter filter);

    Optional<Transaction> findById(UUID id);

    Transaction save(Transaction transaccion);

    void deleteById(UUID id);

    boolean existsByCategoryId(UUID categoryId);
}
