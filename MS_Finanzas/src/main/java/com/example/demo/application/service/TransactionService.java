package com.example.demo.application.service;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.application.usecase.CreateTransactionUseCase;
import com.example.demo.domain.model.Transaction;

@Service
public class TransactionService implements CreateTransactionUseCase{

    private final TransactionRepositoryPort transactionRepositoryPort;

    public TransactionService(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepositoryPort.save(transaction);
    }

}
