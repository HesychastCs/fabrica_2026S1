package com.example.demo.infra.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.example.demo.domain.model.TypeTransaction;

public record TransactionResponse(
    UUID id,
    String nombre,
    BigDecimal monto,
    String descripcion,
    TypeTransaction tipo,
    Instant fechaCreacion,
    String categoriaId,
    String usuarioId
) {

}
