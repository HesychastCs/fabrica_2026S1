package com.example.demo.infra.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.mapper.TransactionEntityMapper;
import com.example.demo.infra.persistence.entity.CategoryEntity;
import com.example.demo.infra.persistence.entity.TitularEntity;
import com.example.demo.infra.persistence.entity.TransactionEntity;

@ExtendWith(MockitoExtension.class)
class JpaTransactionRepositoryAdapterTest {

    @Mock
    JpaTransactionRepository jpaTransactionRepository;

    @Mock
    JpaCategoryRepository jpaCategoryRepository;

    @Mock
    JpaTitularRepository jpaTitularRepository;

    @Mock
    TransactionEntityMapper transactionEntityMapper;

    @InjectMocks
    JpaTransactionRepositoryAdapter adapter;

    @Test
    void findAll_withMonthFilter_mapsEntitiesToDomain() {
        var entity = new TransactionEntity();
        entity.setTransactionId(UUID.randomUUID());
        entity.setFecha(LocalDate.of(2024, 5, 10));

        var domain = new Transaction(entity.getTransactionId(), "n", "d", BigDecimal.TEN, TypeTransaction.GASTO,
                entity.getFecha(), new Category(UUID.randomUUID(), "c", null),
                new Titular(UUID.randomUUID(), "n", null, null, null, null, "USD", "GMT", "tkn"));

        when(jpaTransactionRepository.findFiltered(any(), any(), any(), any())).thenReturn(List.of(entity));
        when(transactionEntityMapper.toDomain(entity)).thenReturn(domain);

        var filter = new TransactionListFilter(Optional.empty(), Optional.empty(), Optional.of(YearMonth.of(2024, 5)));

        var result = adapter.findAll(filter);

        assertEquals(1, result.size());
        assertEquals(domain, result.get(0));
        verify(jpaTransactionRepository).findFiltered(any(), any(), any(), any());
    }

    @Test
    void findById_existing_returnsMappedOptional() {
        var id = UUID.randomUUID();
        var entity = new TransactionEntity();
        entity.setTransactionId(id);
        var domain = new Transaction(id, "n", "d", BigDecimal.ONE, TypeTransaction.INGRESO, LocalDate.now(),
                new Category(UUID.randomUUID(), "c", null), new Titular(UUID.randomUUID(), "n", null, null, null, null, "USD", "GMT", "tkn"));

        when(jpaTransactionRepository.findById(id)).thenReturn(Optional.of(entity));
        when(transactionEntityMapper.toDomain(entity)).thenReturn(domain);

        var opt = adapter.findById(id);

        assertTrue(opt.isPresent());
        assertEquals(domain, opt.get());
    }

    @Test
    void save_newTransaction_createsAndReturnsDomain() {
        var catId = UUID.randomUUID();
        var titId = UUID.randomUUID();
        var domainTx = new Transaction(null, "n", "d", BigDecimal.valueOf(5), TypeTransaction.GASTO,
                LocalDate.of(2024, 6, 1), new Category(catId, "c", null), new Titular(titId, "n", null, null, null, null, "EUR", "GMT", "tkn"));

        var entity = new TransactionEntity();
        var saved = new TransactionEntity();
        saved.setTransactionId(UUID.randomUUID());

        var categoryEntity = new CategoryEntity();
        categoryEntity.setCategoriaId(catId);
        var titularEntity = new TitularEntity();
        titularEntity.setTitularId(titId);

        when(transactionEntityMapper.toEntity(domainTx)).thenReturn(entity);
        when(jpaCategoryRepository.findById(catId)).thenReturn(Optional.of(categoryEntity));
        when(jpaTitularRepository.findById(titId)).thenReturn(Optional.of(titularEntity));
        when(jpaTransactionRepository.save(entity)).thenReturn(saved);
        when(transactionEntityMapper.toDomain(saved)).thenReturn(new Transaction(saved.getTransactionId(), "n", "d", domainTx.monto(), domainTx.tipo(), domainTx.fecha(), domainTx.categoria(), domainTx.titular()));

        var out = adapter.save(domainTx);

        assertNotNull(out.transactionId());
        verify(jpaTransactionRepository).save(entity);
    }

    @Test
    void save_existingTransaction_updates() {
        var id = UUID.randomUUID();
        var existing = new TransactionEntity();
        existing.setTransactionId(id);
        existing.setNombre("old");

        var domainTx = new Transaction(id, "newname", "desc", BigDecimal.valueOf(10), TypeTransaction.GASTO, LocalDate.now(),
                new Category(UUID.randomUUID(), "c", null), new Titular(UUID.randomUUID(), "n", null, null, null, null, "USD", "GMT", "tkn"));

        when(jpaTransactionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(jpaCategoryRepository.findById(any())).thenReturn(Optional.of(new CategoryEntity()));
        when(jpaTitularRepository.findById(any())).thenReturn(Optional.of(new TitularEntity()));
        when(jpaTransactionRepository.save(existing)).thenReturn(existing);
        when(transactionEntityMapper.toDomain(existing)).thenReturn(domainTx);

        var out = adapter.save(domainTx);

        assertEquals(domainTx, out);
        assertEquals("newname", existing.getNombre());
    }

    @Test
    void deleteById_existing_deletesSuccessfully() {
        var id = UUID.randomUUID();
        when(jpaTransactionRepository.existsById(id)).thenReturn(true);

        adapter.deleteById(id);

        verify(jpaTransactionRepository).deleteById(id);
    }

    @Test
    void deleteById_missing_throws() {
        var id = UUID.randomUUID();
        when(jpaTransactionRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adapter.deleteById(id));
    }

    @Test
    void existsByCategoryId_delegates() {
        var catId = UUID.randomUUID();
        when(jpaTransactionRepository.existsByCategoryId(catId)).thenReturn(true);
        assertTrue(adapter.existsByCategoryId(catId));
        verify(jpaTransactionRepository).existsByCategoryId(catId);
    }

    @Test
    void sums_delegate_to_repository() {
        var titId = UUID.randomUUID();
        when(jpaTransactionRepository.sumByTitularAndType(titId, TypeTransaction.GASTO)).thenReturn(BigDecimal.TEN);
        when(jpaTransactionRepository.sumByTitularAndTypeAndMonth(titId, TypeTransaction.GASTO, 6, 2024)).thenReturn(BigDecimal.ONE);

        assertEquals(BigDecimal.TEN, adapter.sumByTitularAndType(titId, TypeTransaction.GASTO));
        assertEquals(BigDecimal.ONE, adapter.sumByTitularAndTypeAndMonth(titId, TypeTransaction.GASTO, 6, 2024));
    }
}
