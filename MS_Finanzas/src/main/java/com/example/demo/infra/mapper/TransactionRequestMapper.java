package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.rest.dto.TransactionRequest;

@Mapper(componentModel = "spring")
public interface TransactionRequestMapper {

    @Mapping(target = "id", ignore = true)
    TransactionRequest toRequest(Transaction transaction);

    Transaction toDomain(TransactionRequest transactionRequest);
}
