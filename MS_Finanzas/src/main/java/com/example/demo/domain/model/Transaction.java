package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Transaction(
    UUID id,
    String nombre,
    String descripcion,
    BigDecimal monto,
    TypeTransaction tipo,
    Instant fechaCreacion,
    UUID categoriaId,
    UUID usuarioId
) {

}
