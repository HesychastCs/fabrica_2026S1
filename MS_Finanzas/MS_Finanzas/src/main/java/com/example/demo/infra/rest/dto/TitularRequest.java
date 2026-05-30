package com.example.demo.infra.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record TitularRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    String primerApellido,
    String segundoApellido,
    String telefono,

    @NotBlank(message = "La moneda preferida es obligatoria")
    String monedaPreferida,

    @NotBlank(message = "La zona horaria es obligatoria")
    String zonaHoraria
) {
}
