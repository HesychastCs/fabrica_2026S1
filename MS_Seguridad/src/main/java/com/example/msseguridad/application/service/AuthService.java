package com.example.msseguridad.application.service;

import com.example.msseguridad.domain.exception.CredencialesInvalidasException;
import com.example.msseguridad.domain.exception.UsuarioYaExisteException;
import com.example.msseguridad.domain.port.in.IniciarSesionUseCase;
import com.example.msseguridad.domain.port.in.RegistrarUsuarioUseCase;
import com.example.msseguridad.domain.port.out.RolRepositoryPort;
import com.example.msseguridad.domain.port.out.UsuarioRepositoryPort;
import com.example.msseguridad.infrastructure.persistence.entity.RolEntity;
import com.example.msseguridad.infrastructure.persistence.entity.UsuarioEntity;
import com.example.msseguridad.infrastructure.rest.dto.AuthResponse;
import com.example.msseguridad.infrastructure.rest.dto.LoginRequest;
import com.example.msseguridad.infrastructure.rest.dto.RegisterRequest;
import com.example.msseguridad.infrastructure.rest.dto.UsuarioResponse;
import com.example.msseguridad.infrastructure.security.JwtUtil;
import com.example.msseguridad.infrastructure.security.SecurityEventLogger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthService implements RegistrarUsuarioUseCase, IniciarSesionUseCase {

    private static final int MAX_INTENTOS = 5;
    private static final String IP_DESCONOCIDA = "unknown";

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RolRepositoryPort rolRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SecurityEventLogger securityEventLogger;

    public AuthService(UsuarioRepositoryPort usuarioRepositoryPort,
                       RolRepositoryPort rolRepositoryPort,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       SecurityEventLogger securityEventLogger) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.rolRepositoryPort = rolRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.securityEventLogger = securityEventLogger;
    }

    @Override
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepositoryPort.existsByUsername(request.getUsername())) {
            throw new UsuarioYaExisteException(
                    "El username '" + request.getUsername() + "' ya existe");
        }
        if (usuarioRepositoryPort.existsByEmail(request.getEmail())) {
            throw new UsuarioYaExisteException(
                    "El email '" + request.getEmail() + "' ya está registrado");
        }

        RolEntity rolUser = rolRepositoryPort.findByNombre("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Rol ROLE_USER no encontrado"));

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(Set.of(rolUser));

        usuarioRepositoryPort.save(usuario);
        securityEventLogger.registroUsuario(request.getUsername(), request.getEmail());

        String token = jwtUtil.generateToken(usuario);
        return buildResponse(token, usuario);
    }

    @Override
    public AuthResponse iniciarSesion(LoginRequest request) {
        UsuarioEntity usuario = usuarioRepositoryPort.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    securityEventLogger.loginFallido(request.getUsername(), IP_DESCONOCIDA,
                            "Usuario no existe");
                    return new CredencialesInvalidasException(
                            "Username o contraseña incorrectos");
                });

        if (usuario.isBloqueado()) {
            securityEventLogger.loginFallido(request.getUsername(), IP_DESCONOCIDA,
                    "Cuenta bloqueada");
            throw new CredencialesInvalidasException(
                    "Cuenta bloqueada por multiples intentos fallidos. Contacta al administrador.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException | LockedException e) {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() >= MAX_INTENTOS) {
                usuario.setBloqueado(true);
                usuarioRepositoryPort.save(usuario);
                securityEventLogger.cuentaBloqueada(request.getUsername());
                throw new CredencialesInvalidasException(
                        "Cuenta bloqueada por " + MAX_INTENTOS +
                        " intentos fallidos. Contacta al administrador.");
            }
            usuarioRepositoryPort.save(usuario);
            securityEventLogger.loginFallido(request.getUsername(), IP_DESCONOCIDA,
                    "Credenciales incorrectas. Intento " + usuario.getIntentosFallidos() +
                    "/" + MAX_INTENTOS);
            throw new CredencialesInvalidasException(
                    "Username o contrasena incorrectos. Intento "
                    + usuario.getIntentosFallidos() + "/" + MAX_INTENTOS);
        }

        usuario.setIntentosFallidos(0);
        usuarioRepositoryPort.save(usuario);
        securityEventLogger.loginExitoso(request.getUsername(), IP_DESCONOCIDA);

        String token = jwtUtil.generateToken(usuario);
        return buildResponse(token, usuario);
    }

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepositoryPort.findAll().stream()
                .map(u -> new UsuarioResponse(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.isBloqueado(),
                        u.getIntentosFallidos(),
                        u.getCreatedAt(),
                        u.getRoles().stream()
                                .map(RolEntity::getNombre)
                                .toList()
                ))
                .toList();
    }

    private AuthResponse buildResponse(String token, UsuarioEntity usuario) {
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(usuario.getRoles().stream()
                        .map(RolEntity::getNombre)
                        .toList())
                .build();
    }
}