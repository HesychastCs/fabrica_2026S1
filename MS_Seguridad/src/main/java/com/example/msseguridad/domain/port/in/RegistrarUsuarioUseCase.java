package com.example.msseguridad.domain.port.in;

import com.example.msseguridad.infrastructure.rest.dto.AuthResponse;
import com.example.msseguridad.infrastructure.rest.dto.RegisterRequest;

public interface RegistrarUsuarioUseCase {
    AuthResponse registrar(RegisterRequest request);
}