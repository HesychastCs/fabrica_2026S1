package com.example.demo.infra.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.persistence.entity.TransactionEntity;

@Mapper(componentModel = "spring")
public interface TransactionEntityMapper {

    @Mapping(target = "categoria", ignore = true)
    TransactionEntity toEntity(Transaction transaction);
    
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "usuarioId", source = "usuarioId")
    Transaction toDomain(TransactionEntity transactionEntity);

    default UUID stringToUuid(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }
}
