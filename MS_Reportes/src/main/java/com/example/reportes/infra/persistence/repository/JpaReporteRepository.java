package com.example.reportes.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.reportes.infra.persistence.entity.ReporteEntity;

@Repository
public interface JpaReporteRepository extends JpaRepository<ReporteEntity, UUID> {

    Optional<ReporteEntity> findByTitularIdAndMesAndAnho(UUID titularId, Integer mes, Integer anho);

    @EntityGraph(attributePaths = {"gastosCategoria", "movimientos"})
    Optional<ReporteEntity> findDetailedByReporteId(UUID reporteId);

    @EntityGraph(attributePaths = {"gastosCategoria", "movimientos"})
    Optional<ReporteEntity> findDetailedByTitularIdAndMesAndAnho(UUID titularId, Integer mes, Integer anho);
}
