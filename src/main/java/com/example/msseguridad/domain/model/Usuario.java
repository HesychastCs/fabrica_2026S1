package com.example.msseguridad.domain.model;

import java.util.Set;

public record Usuario(
    Long id,
    String username,
    String email,
    String password,
    Set<Rol> roles
) {}