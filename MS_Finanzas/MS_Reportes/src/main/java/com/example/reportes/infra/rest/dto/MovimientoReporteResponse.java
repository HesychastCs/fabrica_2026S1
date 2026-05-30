package com.example.reportes.infra.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.reportes.domain.model.TransactionType;

public record MovimientoReporteResponse(
    UUID transaccionId,
    TransactionType tipo,
    String nombre,
    String descripcion,
    BigDecimal monto,
    LocalDate fechaPago,
    String categoriaNombre
) {
}
