package com.example.reportes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FinanzasTransaction(
    UUID transactionId,
    String nombre,
    BigDecimal monto,
    String descripcion,
    TransactionType tipo,
    LocalDate fecha,
    String nombreCategoria
) {
}
