package com.example.reportes.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record GastoPorCategoria(
    UUID categoriaId,
    String categoriaNombre,
    BigDecimal montoTotal,
    BigDecimal porcentajeDelTotal
) {
}
