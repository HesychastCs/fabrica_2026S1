package com.example.demo.application.repository;

import java.util.List;
import java.util.UUID;

import com.example.demo.domain.model.Transaction;

public interface TransactionRepositoryPort{
    List<Transaction> findAll();
    Transaction save(Transaction transaccion);
    boolean existsByCategoryId(UUID categoryId);
}
