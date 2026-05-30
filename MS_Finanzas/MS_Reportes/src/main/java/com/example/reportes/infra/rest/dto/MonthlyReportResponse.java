package com.example.reportes.infra.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.example.reportes.domain.model.ReportStatus;

public record MonthlyReportResponse(
    UUID reporteId,
    UUID titularId,
    Integer mes,
    Integer anho,
    BigDecimal ingresosTotal,
    BigDecimal gastosTotal,
    BigDecimal aportesMetaTotal,
    BigDecimal retirosMetaTotal,
    BigDecimal balanceNeto,
    String moneda,
    ReportStatus estado,
    Instant fechaGenerado,
    List<GastoPorCategoriaResponse> gastosPorCategoria,
    List<MovimientoReporteResponse> movimientos
) {
}
