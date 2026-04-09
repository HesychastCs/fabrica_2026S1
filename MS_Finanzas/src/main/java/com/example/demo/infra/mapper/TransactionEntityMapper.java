package com.example.demo.infra.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.persistence.entity.TransactionEntity;

@Mapper(componentModel = "spring")
public interface TransactionEntityMapper {

    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "titular", ignore = true)
    TransactionEntity toEntity(Transaction transaction);
    
    @Mapping(target = "categoriaId", source = "categoria.categoriaId")
    @Mapping(target = "titularId", source = "titular.titularId")
    Transaction toDomain(TransactionEntity transactionEntity);

    default UUID stringToUuid(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }
}
