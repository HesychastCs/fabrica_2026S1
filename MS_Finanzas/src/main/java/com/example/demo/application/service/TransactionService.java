package com.example.demo.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.application.usecase.CreateTransactionUseCase;
import com.example.demo.application.usecase.GetTransactionUseCase;
import com.example.demo.domain.model.Transaction;

@Service
public class TransactionService implements CreateTransactionUseCase, GetTransactionUseCase{

    private final TransactionRepositoryPort transactionRepositoryPort;

    public TransactionService(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepositoryPort.save(transaction);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepositoryPort.findAll();
    }

}
