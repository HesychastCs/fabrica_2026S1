package com.example.msseguridad.domain.port.out;

import com.example.msseguridad.infrastructure.persistence.entity.RolEntity;

import java.util.Optional;

public interface RolRepositoryPort {
    Optional<RolEntity> findByNombre(String nombre);
}
