package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.rest.dto.TransactionRequest;

@Mapper(componentModel = "spring")
public interface TransactionRequestMapper {

    TransactionRequest toRequest(Transaction transaction);

    Transaction toDomain(TransactionRequest transactionRequest);
}
