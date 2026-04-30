package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Report(
    UUID reportId,
    Integer mes,
    Integer anho,
    BigDecimal ingresosAcumulados,
    BigDecimal gastosAcumulados,
    BigDecimal aportesMetaAcumulados,
    BigDecimal balanceNeto,
    Instant fechaGenerado,
    UUID titularId
) {

}
