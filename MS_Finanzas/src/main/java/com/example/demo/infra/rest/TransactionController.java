package com.example.demo.infra.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.service.TransactionService;
import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.mapper.TransactionRequestMapper;
import com.example.demo.infra.mapper.TransactionResponseMapper;
import com.example.demo.infra.rest.dto.TransactionRequest;
import com.example.demo.infra.rest.dto.TransactionResponse;



@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRequestMapper transactionRequestMapper;
    private final TransactionResponseMapper transactionResponseMapper;

    public TransactionController(TransactionService transactionService, TransactionRequestMapper transactionRequestMapper, TransactionResponseMapper transactionResponseMapper) {
        this.transactionService = transactionService;
        this.transactionRequestMapper = transactionRequestMapper;
        this.transactionResponseMapper = transactionResponseMapper;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.findAll();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
    

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest transactionRequest) {
        Transaction transaction = transactionRequestMapper.toDomain(transactionRequest);
        Transaction transactionCreated = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(transactionResponseMapper.toResponse(transactionCreated), HttpStatus.CREATED);
    }
    
}
