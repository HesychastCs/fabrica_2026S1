package com.example.msseguridad.application.service;

import com.example.msseguridad.infrastructure.persistence.entity.RolEntity;
import com.example.msseguridad.infrastructure.persistence.repository.JpaRolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final JpaRolRepository jpaRolRepository;

    public DataInitializer(JpaRolRepository jpaRolRepository) {
        this.jpaRolRepository = jpaRolRepository;
    }

    @Override
    public void run(String... args) {
        seedRol("ROLE_USER");
        seedRol("ROLE_ADMIN");
    }

    private void seedRol(String nombre) {
        if (jpaRolRepository.findByNombre(nombre).isEmpty()) {
            RolEntity rol = new RolEntity();
            rol.setNombre(nombre);
            jpaRolRepository.save(rol);
            System.out.println("Rol '" + nombre + "' creado.");
        }
    }
}