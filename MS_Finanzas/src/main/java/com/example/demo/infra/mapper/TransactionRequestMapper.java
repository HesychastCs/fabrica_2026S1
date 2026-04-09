package com.example.demo.infra.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.rest.dto.TransactionRequest;

@Mapper(componentModel = "spring")
public interface TransactionRequestMapper {

    TransactionRequest toRequest(Transaction transaction);

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    Transaction toDomain(TransactionRequest transactionRequest);

    default UUID stringToUuid(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }
}
