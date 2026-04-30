package com.example.demo.application.usecase;

import java.util.UUID;

import com.example.demo.domain.model.Transaction;

public interface UpdateTransactionUseCase {

    Transaction updateTransaction(UUID id, Transaction transaction);
}
