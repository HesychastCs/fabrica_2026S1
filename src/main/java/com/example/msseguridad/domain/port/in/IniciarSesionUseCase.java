package com.example.msseguridad.domain.port.in;

import com.example.msseguridad.infrastructure.rest.dto.AuthResponse;
import com.example.msseguridad.infrastructure.rest.dto.LoginRequest;

public interface IniciarSesionUseCase {
    AuthResponse iniciarSesion(LoginRequest request);
}