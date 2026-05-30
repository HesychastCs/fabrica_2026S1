package com.example.demo.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Titular;
import com.example.demo.infra.persistence.entity.CategoryEntity;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    boolean existsByNombreIgnoreCaseAndTitular_TitularId(String name, UUID titularId);
    boolean existsByTitular(Titular titular);

    Optional<CategoryEntity> findByNombreIgnoreCase(String nombre);
}
