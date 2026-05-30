package com.example.msseguridad.infrastructure.security;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    private final Set<String> tokensBloqueados = Collections.synchronizedSet(new HashSet<>());

    public void bloquear(String token) {
        tokensBloqueados.add(token);
    }

    public boolean estaBloqueado(String token) {
        return tokensBloqueados.contains(token);
    }
}