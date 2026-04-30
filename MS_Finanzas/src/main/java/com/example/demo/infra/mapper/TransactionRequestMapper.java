package com.example.demo.infra.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;
import com.example.demo.infra.rest.dto.TransactionRequest;

@Mapper(componentModel = "spring")
public interface TransactionRequestMapper {

    default TransactionRequest toRequest(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        String categoriaId = transaction.categoria() != null && transaction.categoria().categoriaId() != null
            ? transaction.categoria().categoriaId().toString()
            : null;
        String titularId = transaction.titular() != null && transaction.titular().titularId() != null
            ? transaction.titular().titularId().toString()
            : null;
        return new TransactionRequest(
            transaction.nombre(),
            transaction.monto(),
            transaction.descripcion(),
            transaction.tipo(),
            transaction.fecha(),
            categoriaId,
            titularId
        );
    }

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "categoria", expression = "java(mapCategory(request.categoriaId()))")
    @Mapping(target = "titular", expression = "java(mapTitular(request.titularId()))")
    @Mapping(target = "fecha", source = "fecha")
    Transaction toDomain(TransactionRequest request);

    default Category mapCategory(String categoriaId) {
        if (categoriaId == null || categoriaId.isBlank()) {
            return null;
        }
        return new Category(UUID.fromString(categoriaId.trim()), null, null);
    }

    default Titular mapTitular(String titularId) {
        return new Titular(UUID.fromString(titularId.trim()), null, null, null, null, null, null, null, null);
    }
}
