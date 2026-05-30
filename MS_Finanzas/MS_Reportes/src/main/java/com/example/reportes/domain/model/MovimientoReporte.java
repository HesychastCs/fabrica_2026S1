package com.example.reportes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MovimientoReporte(
    UUID transaccionId,
    TransactionType tipo,
    String nombre,
    String descripcion,
    BigDecimal monto,
    LocalDate fechaPago,
    String categoriaNombre
) {
}
