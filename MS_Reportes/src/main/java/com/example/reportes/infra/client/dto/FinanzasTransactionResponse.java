package com.example.reportes.infra.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FinanzasTransactionResponse(
    UUID transactionId,
    String nombre,
    BigDecimal monto,
    String descripcion,
    String tipo,
    LocalDate fecha,
    String nombreCategoria
) {
}
