package com.example.demo.infra.rest.dto;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReportRequest(
    @Valid
    @NotNull(message="El mes es obligatorio")
    @Min(value = 1, message="El mes es invalido")
    @Max(value = 12, message="El mes es invalido")
    Integer mes,
    @Valid
    @NotNull(message="El año es obligatorio")
    @Min(value = 1900, message="El año ingresado es invalido")
    @Max(value = 2100, message="El año ingresado es invalido")
    Integer anho,
    UUID titularId
) {

}
