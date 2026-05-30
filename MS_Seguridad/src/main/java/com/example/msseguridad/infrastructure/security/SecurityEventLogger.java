package com.example.msseguridad.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SecurityEventLogger {

    private static final Logger log = LoggerFactory.getLogger("SECURITY_EVENTS");

    public void loginExitoso(String username, String ip) {
        log.info("{\"evento\":\"LOGIN_EXITOSO\",\"usuario\":\"{}\",\"ip\":\"{}\",\"timestamp\":\"{}\"}",
                username, ip, Instant.now());
    }

    public void loginFallido(String username, String ip, String motivo) {
        log.warn("{\"evento\":\"LOGIN_FALLIDO\",\"usuario\":\"{}\",\"ip\":\"{}\",\"motivo\":\"{}\",\"timestamp\":\"{}\"}",
                username, ip, motivo, Instant.now());
    }

    public void cuentaBloqueada(String username) {
        log.warn("{\"evento\":\"CUENTA_BLOQUEADA\",\"usuario\":\"{}\",\"timestamp\":\"{}\"}",
                username, Instant.now());
    }

    public void logoutExitoso(String username) {
        log.info("{\"evento\":\"LOGOUT_EXITOSO\",\"usuario\":\"{}\",\"timestamp\":\"{}\"}",
                username, Instant.now());
    }

    public void registroUsuario(String username, String email) {
        log.info("{\"evento\":\"REGISTRO_USUARIO\",\"usuario\":\"{}\",\"email\":\"{}\",\"timestamp\":\"{}\"}",
                username, email, Instant.now());
    }
}