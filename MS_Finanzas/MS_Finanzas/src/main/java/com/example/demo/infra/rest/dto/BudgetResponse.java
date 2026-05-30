package com.example.demo.infra.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BudgetResponse(
    UUID presupuestoId,
    BigDecimal montoLimite,
    LocalDate fechaInicio,
    LocalDate fechaFinal,
    Instant fechaCreacion,
    String nombreTitular
) {

}
