package com.example.demo.infra.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.rest.dto.TransactionResponse;

@Mapper(componentModel = "spring")
public interface TransactionResponseMapper {

    @Mapping(target = "nombreCategoria", source = "categoria.nombre")
    @Mapping(target = "nombreTitular", source = "titular.nombre")
    TransactionResponse toResponse(Transaction transaction);

    default UUID stringToUuid(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }
}

