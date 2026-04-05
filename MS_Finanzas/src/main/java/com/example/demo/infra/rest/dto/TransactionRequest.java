package com.example.demo.infra.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.demo.domain.model.TypeTransaction;

public record TransactionRequest(
    Long id,
    String nombre,
    BigDecimal monto,
    String descripcion,
    TypeTransaction tipoTransaccion,
    Instant fechaTransaccion
) {

}
