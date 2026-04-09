package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record Transaction(
    UUID transactionId,
    String nombre,
    String descripcion,
    BigDecimal monto,
    TypeTransaction tipo,
    LocalDate fecha,
    UUID categoriaId,
    UUID titularId
) {

}
