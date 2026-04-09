package com.example.demo.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Titular(
    UUID titularId,
    String nombre,
    String primerApellido,
    String segundoApellido,
    Instant fechaRegistro,
    String token
) {

}
