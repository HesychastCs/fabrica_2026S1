package com.example.demo.infra.rest.dto;

import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record SavingGoalRequest(

    @Schema(description = "Nombre de la meta de ahorro", example = "Viaje a Cuba", required = true)
    String nombre,

    @Schema(description = "Monto objetivo en pesos colombianos", example = "12000000", required = true)
    Double montoObjetivo,

    @Schema(description = "Fecha límite para alcanzar la meta (formato YYYY-MM-DD), opcional", example = "2026-12-31")
    LocalDate fechaLimite,

    @Schema(
        description = "ID del titular financiero (debe existir en la base de datos)",
        example = "aa8a8b1d-e583-4168-97f6-64e6a6986397",
        required = true
    )
    UUID titularId

) {}