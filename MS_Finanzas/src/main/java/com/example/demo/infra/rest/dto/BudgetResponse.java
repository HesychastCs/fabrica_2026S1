package com.example.demo.infra.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(
    UUID presupuestoId,
    BigDecimal montoLimite,
    BigDecimal gastoAcumulado,
    BigDecimal montoDisponible
) {

}
