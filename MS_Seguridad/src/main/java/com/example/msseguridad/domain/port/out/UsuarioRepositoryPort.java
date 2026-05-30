package com.example.msseguridad.domain.port.out;

import com.example.msseguridad.infrastructure.persistence.entity.UsuarioEntity;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<UsuarioEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void save(UsuarioEntity entity);
    UsuarioEntity saveAndReturn(UsuarioEntity entity);
    List<UsuarioEntity> findAll();
}