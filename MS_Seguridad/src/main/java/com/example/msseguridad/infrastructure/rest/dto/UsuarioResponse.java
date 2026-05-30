package com.example.msseguridad.infrastructure.rest.dto;

import java.time.Instant;
import java.util.List;

public record UsuarioResponse(
    Long id,
    String username,
    String email,
    boolean bloqueado,
    int intentosFallidos,
    Instant createdAt,
    List<String> roles
) {}