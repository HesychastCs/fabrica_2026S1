package com.example.reportes.infra.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record GastoPorCategoriaResponse(
    UUID categoriaId,
    String categoriaNombre,
    BigDecimal montoTotal,
    BigDecimal porcentajeDelTotal
) {
}
