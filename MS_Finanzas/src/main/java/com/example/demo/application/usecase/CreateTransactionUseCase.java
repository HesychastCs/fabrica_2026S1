package com.example.demo.application.usecase;

import com.example.demo.domain.model.Transaction;

public interface CreateTransactionUseCase {
    Transaction createTransaction(Transaction transaction);
}
