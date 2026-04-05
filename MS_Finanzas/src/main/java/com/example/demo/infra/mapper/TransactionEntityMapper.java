package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.persistence.entity.TransactionEntity;

@Mapper(componentModel = "spring")
public interface TransactionEntityMapper {

    TransactionEntity toEntity(Transaction transaction);
    Transaction toDomain(TransactionEntity transactionEntity);
}
