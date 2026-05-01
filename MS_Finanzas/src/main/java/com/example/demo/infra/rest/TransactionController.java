package com.example.demo.infra.rest;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.application.service.TransactionService;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.mapper.TransactionRequestMapper;
import com.example.demo.infra.mapper.TransactionResponseMapper;
import com.example.demo.infra.rest.dto.TransactionRequest;
import com.example.demo.infra.rest.dto.TransactionResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRequestMapper transactionRequestMapper;
    private final TransactionResponseMapper transactionResponseMapper;

    public TransactionController(
        TransactionService transactionService,
        TransactionRequestMapper transactionRequestMapper,
        TransactionResponseMapper transactionResponseMapper
    ) {
        this.transactionService = transactionService;
        this.transactionRequestMapper = transactionRequestMapper;
        this.transactionResponseMapper = transactionResponseMapper;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> list(
        @RequestParam(required = false) TypeTransaction tipo,
        @RequestParam(required = false) UUID categoriaId,
        @RequestParam(required = false) String mes
    ) {
        Optional<YearMonth> yearMonth = Optional.ofNullable(mes).filter(s -> !s.isBlank()).map(YearMonth::parse);
        TransactionListFilter filter = new TransactionListFilter(
            Optional.ofNullable(tipo),
            Optional.ofNullable(categoriaId),
            yearMonth
        );
        List<Transaction> transactions = transactionService.findAll(filter);
        return ResponseEntity.ok(
            transactions.stream().map(transactionResponseMapper::toResponse).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable UUID id) {
        return transactionService.findById(id)
            .map(transactionResponseMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        Transaction created = transactionService.createTransaction(transactionRequestMapper.toDomain(request));
        return new ResponseEntity<>(transactionResponseMapper.toResponse(created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
        @PathVariable UUID id,
        @Valid @RequestBody TransactionRequest request
    ) {
        Transaction updated = transactionService.updateTransaction(id, transactionRequestMapper.toDomain(request));
        return ResponseEntity.ok(transactionResponseMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
