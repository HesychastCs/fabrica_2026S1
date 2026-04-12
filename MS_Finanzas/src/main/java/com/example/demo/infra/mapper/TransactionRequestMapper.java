package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.rest.dto.TransactionRequest;

@Mapper(componentModel = "spring")
public interface TransactionRequestMapper {

    @Mapping(target = "categoriaId", source = "categoria.categoriaId")
    @Mapping(target = "titularId", source = "titular.titularId")
    TransactionRequest toRequest(Transaction transaction);

    @Mapping(target = "categoria.categoriaId", source = "categoriaId")
    @Mapping(target = "titular.titularId", source = "titularId")
    @Mapping(target = "fecha", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    Transaction toDomain(TransactionRequest transactionRequest);

}
