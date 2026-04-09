package com.example.demo.infra.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.demo.domain.model.TypeTransaction;

public record TransactionResponse(
    UUID transactionId,
    String nombre,
    BigDecimal monto,
    String descripcion,
    TypeTransaction tipo,
    LocalDate fecha,
    String categoriaId,
    String titularId
) {

}
