package com.example.reportes.infra.rest.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MonthlyReportRequest(
    @NotNull(message = "El titularId es obligatorio")
    UUID titularId,
    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes es inválido")
    @Max(value = 12, message = "El mes es inválido")
    Integer mes,
    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año es inválido")
    @Max(value = 2100, message = "El año es inválido")
    Integer anho,
    String moneda
) {
}
