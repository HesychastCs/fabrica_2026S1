package com.example.demo.infra.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.mapper.TransactionEntityMapper;
import com.example.demo.infra.persistence.entity.TransactionEntity;

@Component
public class JpaTransactionRepositoryAdapter implements TransactionRepositoryPort{
    private final JpaTransactionRepository jpaTransactionRepository;
    private final TransactionEntityMapper transactionEntityMapper;

    public JpaTransactionRepositoryAdapter(JpaTransactionRepository jpaTransactionRepository, TransactionEntityMapper transactionEntityMapper) {
        this.jpaTransactionRepository = jpaTransactionRepository;
        this.transactionEntityMapper = transactionEntityMapper;
    }

    @Override
    public List<Transaction> findAll() {
        return jpaTransactionRepository.findAll().stream().map(transactionEntityMapper::toDomain).toList();
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity transactionEntity = transactionEntityMapper.toEntity(transaction);
        TransactionEntity savedTransactionEntity = jpaTransactionRepository.save(transactionEntity);
        return transactionEntityMapper.toDomain(savedTransactionEntity);
    }

    @Override
    public boolean existsByCategoryId(UUID categoryId) {
        return jpaTransactionRepository.existsByCategoryId(categoryId);
    }

}
