package com.example.msseguridad.infrastructure.persistence.repository;

import com.example.msseguridad.domain.port.out.UsuarioRepositoryPort;
import com.example.msseguridad.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final JpaUsuarioRepository jpaUsuarioRepository;

    public UsuarioRepositoryAdapter(JpaUsuarioRepository jpaUsuarioRepository) {
        this.jpaUsuarioRepository = jpaUsuarioRepository;
    }

    @Override
    public Optional<UsuarioEntity> findByUsername(String username) {
        return jpaUsuarioRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUsuarioRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUsuarioRepository.existsByEmail(email);
    }

    @Override
    @SuppressWarnings("null")
    public void save(UsuarioEntity entity) {
        jpaUsuarioRepository.save(entity);
    }

    @Override
    @SuppressWarnings("null")
    public UsuarioEntity saveAndReturn(UsuarioEntity entity) {
        return jpaUsuarioRepository.save(entity);
    }

    @Override
    public List<UsuarioEntity> findAll() {
        return jpaUsuarioRepository.findAll();
    }
}