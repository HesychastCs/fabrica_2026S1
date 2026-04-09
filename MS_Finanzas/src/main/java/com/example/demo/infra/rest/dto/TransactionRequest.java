package com.example.demo.infra.rest.dto;

import java.math.BigDecimal;

import com.example.demo.domain.model.TypeTransaction;

public record TransactionRequest(
    String nombre,
    BigDecimal monto,
    String descripcion,
    TypeTransaction tipo,
    String categoriaId,
    String titularId
) {

}
