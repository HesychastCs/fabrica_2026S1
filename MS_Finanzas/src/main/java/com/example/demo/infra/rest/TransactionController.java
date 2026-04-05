package com.example.demo.infra.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.usecase.CreateTransactionUseCase;
import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.mapper.TransactionRequestMapper;
import com.example.demo.infra.mapper.TransactionResponseMapper;
import com.example.demo.infra.rest.dto.TransactionRequest;
import com.example.demo.infra.rest.dto.TransactionResponse;


@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final TransactionRequestMapper transactionRequestMapper;
    private final TransactionResponseMapper transactionResponseMapper;

    public TransactionController(com.example.demo.application.usecase.CreateTransactionUseCase createTransactionUseCase, com.example.demo.infra.mapper.TransactionRequestMapper transactionRequestMapper, com.example.demo.infra.mapper.TransactionResponseMapper transactionResponseMapper) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.transactionRequestMapper = transactionRequestMapper;
        this.transactionResponseMapper = transactionResponseMapper;
    }

    @PostMapping
    public TransactionResponse createTransaction(@RequestBody TransactionRequest transactionRequest) {
        Transaction transaction = transactionRequestMapper.toDomain(transactionRequest);
        Transaction transactionCreated = createTransactionUseCase.createTransaction(transaction);
        return transactionResponseMapper.toResponse(transactionCreated);
    }
    
}
