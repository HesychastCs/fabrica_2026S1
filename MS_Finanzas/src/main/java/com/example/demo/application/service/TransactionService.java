package com.example.demo.application.service;

import org.springframework.stereotype.Service;

import com.example.demo.application.usecase.CreateTransactionUseCase;
import com.example.demo.domain.model.Transaction;

@Service
public class TransactionService implements CreateTransactionUseCase{

    @Override
    public Transaction createTransaction(Transaction transaction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
