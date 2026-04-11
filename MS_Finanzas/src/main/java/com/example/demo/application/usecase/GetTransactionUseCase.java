package com.example.demo.application.usecase;

import java.util.List;

import com.example.demo.domain.model.Transaction;

public interface GetTransactionUseCase {
    List<Transaction> findAll();
}
