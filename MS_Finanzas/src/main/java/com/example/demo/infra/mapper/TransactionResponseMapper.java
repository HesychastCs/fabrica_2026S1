package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.rest.dto.TransactionResponse;

@Mapper(componentModel = "spring")
public interface TransactionResponseMapper {

    TransactionResponse toResponse(Transaction transaction);

    Transaction toDomain(TransactionResponse transactionResponse);
}

