package com.example.demo.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.EmptyCategoryConstants;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private TitularRepositoryPort titularRepositoryPort;

    @InjectMocks
    private TransactionService transactionService;

    private UUID titularId;
    private UUID categoryId;
    private Titular titular;
    private Category category;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        titular = new Titular(titularId, "Luis", "Martínez", "Lopez", "3214567890", null, "USD", "America/New_York", "token");
        category = new Category(categoryId, "Taxi", titular);
    }

    @Test
    void createTransaction_shouldSaveTransactionWithExistingCategory() {
        Transaction partial = new Transaction(null, "Viaje", "Taxi aeropuerto", BigDecimal.valueOf(12000), TypeTransaction.GASTO, LocalDate.now(), category, titular);
        Transaction saved = new Transaction(UUID.randomUUID(), "Viaje", "Taxi aeropuerto", BigDecimal.valueOf(12000), TypeTransaction.GASTO, LocalDate.now(), category, titular);

        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.of(titular));
        given(categoryRepositoryPort.findById(categoryId)).willReturn(Optional.of(category));
        given(transactionRepositoryPort.save(any(Transaction.class))).willReturn(saved);

        Transaction result = transactionService.createTransaction(partial);

        assertEquals(saved, result);
        verify(transactionRepositoryPort).save(any(Transaction.class));
    }

    @Test
    void createTransaction_shouldCreateEmptyCategoryWhenNoCategoryProvided() {
        Transaction partial = new Transaction(null, "Pago", "Pago sin categoría", BigDecimal.ONE, TypeTransaction.INGRESO, null, null, titular);
        Category emptyCategory = new Category(null, EmptyCategoryConstants.NAME, null);
        Transaction saved = new Transaction(UUID.randomUUID(), "Pago", "Pago sin categoría", BigDecimal.ONE, TypeTransaction.INGRESO, LocalDate.now(), emptyCategory, titular);

        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.of(titular));
        given(categoryRepositoryPort.findByNombreIgnoreCase(EmptyCategoryConstants.NAME)).willReturn(Optional.empty());
        given(categoryRepositoryPort.save(any(Category.class))).willReturn(emptyCategory);
        given(transactionRepositoryPort.save(any(Transaction.class))).willReturn(saved);

        Transaction result = transactionService.createTransaction(partial);

        assertEquals(saved, result);
        verify(categoryRepositoryPort).save(any(Category.class));
        verify(transactionRepositoryPort).save(any(Transaction.class));
    }

    @Test
    void createTransaction_shouldThrowWhenTitularNotFound() {
        Transaction partial = new Transaction(null, "Pago", "Sin titular", BigDecimal.ONE, TypeTransaction.INGRESO, LocalDate.now(), category, titular);

        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(partial));
    }

    @Test
    void updateTransaction_shouldThrowWhenTransactionDoesNotExist() {
        UUID transactionId = UUID.randomUUID();
        Transaction partial = new Transaction(null, "Viaje", "Taxi", BigDecimal.valueOf(15000), TypeTransaction.GASTO, LocalDate.now(), category, titular);

        given(transactionRepositoryPort.findById(transactionId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(transactionId, partial));
    }
}
