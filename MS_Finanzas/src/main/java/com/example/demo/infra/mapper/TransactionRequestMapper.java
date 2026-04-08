package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.rest.dto.TransactionRequest;

@Mapper(componentModel = "spring")
public interface TransactionRequestMapper {

    TransactionRequest toRequest(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Transaction toDomain(TransactionRequest transactionRequest);
}
