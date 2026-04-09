package com.example.demo.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Titular(
    UUID titularId,
    String nombre,
    String primerApellido,
    String segundoApellido,
    String telefono,
    Instant fechaRegistro,
    String monedaPreferida,
    String zonaHoraria,
    String token
) {

}
