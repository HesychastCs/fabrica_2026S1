package com.example.demo.infra.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record TitularResponse(
    UUID titularId,
    String nombre,
    String primerApellido,
    String segundoApellido,
    String telefono,
    Instant fechaRegistro,
    String monedaPreferida,
    String zonaHoraria
) {
}
