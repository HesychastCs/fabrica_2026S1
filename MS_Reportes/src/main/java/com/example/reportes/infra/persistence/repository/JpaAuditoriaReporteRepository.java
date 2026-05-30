package com.example.reportes.infra.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.reportes.infra.persistence.entity.AuditoriaReporteEntity;

@Repository
public interface JpaAuditoriaReporteRepository extends JpaRepository<AuditoriaReporteEntity, UUID> {
}
