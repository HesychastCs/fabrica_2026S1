package com.example.demo.infra.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
    UUID reportId,
    Integer mes,
    Integer anho,
    BigDecimal ingresosAcumulados,
    BigDecimal gastosAcumulados,
    BigDecimal aportesMetaAcumulados,
    BigDecimal balanceNeto,
    Instant fechaGenerado
) {

}
