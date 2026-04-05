package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
    Long id,
    String nombre,
    BigDecimal monto,
    String descripcion,
    TypeTransaction tipoTransaccion,
    Instant fechaTransaccion
) {

}
