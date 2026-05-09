package com.example.demo.infra.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.application.service.TransactionService;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.mapper.TransactionRequestMapper;
import com.example.demo.infra.mapper.TransactionResponseMapper;
import com.example.demo.infra.rest.dto.TransactionRequest;
import com.example.demo.infra.rest.dto.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TransactionControllerTest {

    private final StubTransactionService transactionService = new StubTransactionService();

    private TransactionController controller;
    private TransactionRequestMapper transactionRequestMapper;
    private TransactionResponseMapper transactionResponseMapper;

    private UUID titularId;
    private UUID categoriaId;
    private Titular titular;
    private Category categoria;
    private Transaction transaction;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setup() {
        titularId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();
        titular = new Titular(titularId, "Juan", "Perez", "Gomez", "3001234567", Instant.now(), "COP", "America/Bogota", "token-1");
        categoria = new Category(categoriaId, "Alimentación", titular);
        transaction = new Transaction(UUID.randomUUID(), "Pago luz", "Pago de energía", BigDecimal.valueOf(150000), TypeTransaction.GASTO, LocalDate.of(2026, 5, 1), categoria, titular);
        transactionResponse = new TransactionResponse(transaction.transactionId(), transaction.nombre(), transaction.monto(), transaction.descripcion(), transaction.tipo(), transaction.fecha(), transaction.categoria().nombre(), transaction.titular().nombre());
        transactionRequestMapper = request -> transaction;
        transactionResponseMapper = value -> transactionResponse;
        transactionService.findAllResult = List.of(transaction);
        transactionService.findByIdResult = Optional.of(transaction);
        transactionService.createResult = transaction;
        transactionService.updateResult = transaction;
        controller = new TransactionController(transactionService, transactionRequestMapper, transactionResponseMapper);
    }

    @Test
    void list_shouldBuildFilterWithoutMonthWhenBlank() {
        ResponseEntity<List<TransactionResponse>> response = controller.list(null, null, "");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(transactionResponse);
        assertThat(transactionService.lastFilter.tipo()).isEmpty();
        assertThat(transactionService.lastFilter.categoriaId()).isEmpty();
        assertThat(transactionService.lastFilter.mes()).isEmpty();
    }

    @Test
    void list_shouldParseYearMonthWhenProvided() {
        ResponseEntity<List<TransactionResponse>> response = controller.list(TypeTransaction.GASTO, categoriaId, "2026-05");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(transactionResponse);
        assertThat(transactionService.lastFilter.tipo()).contains(TypeTransaction.GASTO);
        assertThat(transactionService.lastFilter.categoriaId()).contains(categoriaId);
        assertThat(transactionService.lastFilter.mes()).contains(YearMonth.of(2026, 5));
    }

    @Test
    void getById_shouldReturnOkWhenFound() {
        UUID txId = transaction.transactionId();

        ResponseEntity<TransactionResponse> response = controller.getById(txId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(transactionResponse);
    }

    @Test
    void getById_shouldReturnNotFoundWhenMissing() {
        transactionService.findByIdResult = Optional.empty();

        ResponseEntity<TransactionResponse> response = controller.getById(UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void create_shouldReturnCreatedResponse() {
        TransactionRequest request = new TransactionRequest("Pago agua", BigDecimal.valueOf(80000), "Servicio", TypeTransaction.GASTO, LocalDate.of(2026, 5, 2), categoriaId.toString(), titularId.toString());

        ResponseEntity<TransactionResponse> response = controller.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(transactionResponse);
        assertThat(transactionService.lastCreatedTransaction).isEqualTo(transaction);
    }

    @Test
    void update_shouldReturnOkResponse() {
        UUID txId = transaction.transactionId();
        TransactionRequest request = new TransactionRequest("Pago agua", BigDecimal.valueOf(80000), "Servicio", TypeTransaction.GASTO, LocalDate.of(2026, 5, 2), categoriaId.toString(), titularId.toString());

        ResponseEntity<TransactionResponse> response = controller.update(txId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(transactionResponse);
        assertThat(transactionService.lastUpdatedId).isEqualTo(txId);
        assertThat(transactionService.lastUpdatedTransaction).isEqualTo(transaction);
    }

    @Test
    void delete_shouldCallDeleteAndReturnNoContent() {
        UUID txId = transaction.transactionId();

        ResponseEntity<Void> response = controller.delete(txId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(transactionService.lastDeletedId).isEqualTo(txId);
    }

    private static final class StubTransactionService extends TransactionService {

        private TransactionListFilter lastFilter;
        private Optional<Transaction> findByIdResult = Optional.empty();
        private Transaction lastCreatedTransaction;
        private Transaction lastUpdatedTransaction;
        private UUID lastUpdatedId;
        private UUID lastDeletedId;
        private List<Transaction> findAllResult = List.of();
        private Transaction createResult;
        private Transaction updateResult;

        private StubTransactionService() {
            super(null, null, null);
        }

        @Override
        public List<Transaction> findAll(TransactionListFilter filter) {
            lastFilter = filter;
            return findAllResult;
        }

        @Override
        public Optional<Transaction> findById(UUID id) {
            return findByIdResult;
        }

        @Override
        public Transaction createTransaction(Transaction transaction) {
            lastCreatedTransaction = transaction;
            return createResult;
        }

        @Override
        public Transaction updateTransaction(UUID id, Transaction transaction) {
            lastUpdatedId = id;
            lastUpdatedTransaction = transaction;
            return updateResult;
        }

        @Override
        public void deleteTransaction(UUID id) {
            lastDeletedId = id;
        }
    }
}
