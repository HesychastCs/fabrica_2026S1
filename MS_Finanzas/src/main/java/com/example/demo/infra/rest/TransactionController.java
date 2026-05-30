package com.example.demo.infra.rest;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.application.service.TransactionService;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.mapper.TransactionRequestMapper;
import com.example.demo.infra.mapper.TransactionResponseMapper;
import com.example.demo.infra.rest.dto.TransactionRequest;
import com.example.demo.infra.rest.dto.TransactionResponse;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@CrossOrigin(
        origins = {
                "https://front-end-fe20261.vercel.app",
                "https://front-end-fe20261-c4otfrley-junior-morenos-projects.vercel.app"
        },
        allowedHeaders = "*",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS
        }
)
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRequestMapper transactionRequestMapper;
    private final TransactionResponseMapper transactionResponseMapper;

    public TransactionController(TransactionService transactionService,
                                  TransactionRequestMapper transactionRequestMapper,
                                  TransactionResponseMapper transactionResponseMapper) {
        this.transactionService = transactionService;
        this.transactionRequestMapper = transactionRequestMapper;
        this.transactionResponseMapper = transactionResponseMapper;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<TransactionResponse>>> list(
            @RequestParam(required = false) TypeTransaction tipo,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) String mes) {
        Optional<YearMonth> yearMonth = Optional.ofNullable(mes)
                .filter(s -> !s.isBlank()).map(YearMonth::parse);
        TransactionListFilter filter = new TransactionListFilter(
                Optional.ofNullable(tipo),
                Optional.ofNullable(categoriaId),
                yearMonth);
        List<Transaction> transactions = transactionService.findAll(filter);
        List<EntityModel<TransactionResponse>> responses = transactions.stream()
                .map(transactionResponseMapper::toResponse)
                .map(response -> EntityModel.of(response,
                        linkTo(methodOn(TransactionController.class)
                                .getById(response.transactionId())).withSelfRel(),
                        linkTo(methodOn(TransactionController.class)
                                .list(null, null, null)).withRel("all")))
                .toList();
        CollectionModel<EntityModel<TransactionResponse>> collection = CollectionModel.of(responses,
                linkTo(methodOn(TransactionController.class).list(null, null, null)).withSelfRel());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TransactionResponse>> getById(@PathVariable UUID id) {
        return transactionService.findById(id)
                .map(transactionResponseMapper::toResponse)
                .map(response -> EntityModel.of(response,
                        linkTo(methodOn(TransactionController.class).getById(id)).withSelfRel(),
                        linkTo(methodOn(TransactionController.class)
                                .list(null, null, null)).withRel("all")))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<TransactionResponse>> create(
            @Valid @RequestBody TransactionRequest request) {
        Transaction created = transactionService.createTransaction(
                transactionRequestMapper.toDomain(request));
        TransactionResponse response = transactionResponseMapper.toResponse(created);
        EntityModel<TransactionResponse> model = EntityModel.of(response,
                linkTo(methodOn(TransactionController.class)
                        .getById(response.transactionId())).withSelfRel(),
                linkTo(methodOn(TransactionController.class)
                        .list(null, null, null)).withRel("all"));
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TransactionResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request) {
        Transaction updated = transactionService.updateTransaction(
                id, transactionRequestMapper.toDomain(request));
        TransactionResponse response = transactionResponseMapper.toResponse(updated);
        EntityModel<TransactionResponse> model = EntityModel.of(response,
                linkTo(methodOn(TransactionController.class).getById(id)).withSelfRel(),
                linkTo(methodOn(TransactionController.class)
                        .list(null, null, null)).withRel("all"));
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}