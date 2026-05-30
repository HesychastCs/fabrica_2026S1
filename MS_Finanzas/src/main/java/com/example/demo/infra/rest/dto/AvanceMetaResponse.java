package com.example.demo.infra.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AvanceMetaResponse(
    UUID metaId,
    String nombreMeta,
    BigDecimal montoObjetivo,
    BigDecimal totalAportado,
    BigDecimal porcentajeAvance,
    LocalDate fechaLimite,
    Long diasRestantes,
    LocalDate fechaProyectadaCumplimiento
) {}