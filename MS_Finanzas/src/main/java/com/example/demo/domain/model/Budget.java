package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Budget(
    UUID presupuestoId,
    BigDecimal montoLimite,
    Instant fechaCreacion,
    LocalDate fechaInicio,
    LocalDate fechaFinal,
    BigDecimal gastoAcumulado,
    BigDecimal montoDisponible,
    Titular titular
) {

}
