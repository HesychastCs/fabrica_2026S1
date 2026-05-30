package com.example.reportes.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReporteMensual(
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
    List<GastoPorCategoria> gastosPorCategoria,
    List<MovimientoReporte> movimientos
) {
}
