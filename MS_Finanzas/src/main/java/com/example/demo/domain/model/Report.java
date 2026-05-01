package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.example.demo.infra.persistence.entity.TitularEntity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;

// Domain layer - core business logic
public record Report(
    UUID reportId,
    Integer mes,
    Integer anho,
    @DecimalMin(value="0.00", inclusive=true)
    @Digits(integer=15, fraction=2)
    BigDecimal ingresosAcumulados,
    @DecimalMin(value="0.00", inclusive=true)
    @Digits(integer=15, fraction=2)
    BigDecimal gastosAcumulados,
    @DecimalMin(value="0.00", inclusive=true)
    @Digits(integer=15, fraction=2)
    BigDecimal aportesMetaAcumulados,
    @Digits(integer=15, fraction=2)
    BigDecimal balanceNeto,
    @PastOrPresent(message="La fecha de generación no puede ser futura")
    Instant fechaGenerado,
    Titular titular
) {
    
}
