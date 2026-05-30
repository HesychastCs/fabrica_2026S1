package com.example.msseguridad.infrastructure.rest.controller;

import com.example.msseguridad.application.service.AuthService;
import com.example.msseguridad.infrastructure.rest.dto.AuthResponse;
import com.example.msseguridad.infrastructure.rest.dto.LoginRequest;
import com.example.msseguridad.infrastructure.rest.dto.RegisterRequest;
import com.example.msseguridad.infrastructure.rest.dto.UsuarioResponse;
import com.example.msseguridad.infrastructure.security.JwtUtil;
import com.example.msseguridad.infrastructure.security.SecurityEventLogger;
import com.example.msseguridad.infrastructure.security.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints de registro, login y logout")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;
    private final SecurityEventLogger securityEventLogger;

    public AuthController(AuthService authService,
                          TokenBlacklistService tokenBlacklistService,
                          JwtUtil jwtUtil,
                          SecurityEventLogger securityEventLogger) {
        this.authService = authService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtUtil = jwtUtil;
        this.securityEventLogger = securityEventLogger;
    }

    @PostMapping("/register")
    @Operation(summary = "HU-10: Registrar cuenta de usuario")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "HU-11: Iniciar sesión")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.iniciarSesion(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "HU-12: Cerrar sesión de forma segura")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.bloquear(token);
            try {
                String username = jwtUtil.extractUsername(token);
                securityEventLogger.logoutExitoso(username);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada correctamente"));
    }

    @GetMapping("/admin/usuarios")
    @Operation(summary = "Solo ADMIN: listar todos los usuarios")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(authService.listarUsuarios());
    }
}