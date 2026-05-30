package com.example.msseguridad.infrastructure.persistence.repository;

import com.example.msseguridad.domain.port.out.RolRepositoryPort;
import com.example.msseguridad.infrastructure.persistence.entity.RolEntity;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class RolRepositoryAdapter implements RolRepositoryPort {

    private final JpaRolRepository jpaRolRepository;

    public RolRepositoryAdapter(JpaRolRepository jpaRolRepository) {
        this.jpaRolRepository = jpaRolRepository;
    }

    @Override
    public Optional<RolEntity> findByNombre(String nombre) {
        return jpaRolRepository.findByNombre(nombre);
    }
}