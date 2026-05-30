package com.example.reportes.application.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.reportes.domain.model.ReporteMensual;

public interface ReporteRepositoryPort {

    ReporteMensual save(ReporteMensual reporte);

    Optional<ReporteMensual> findById(UUID reporteId);

    Optional<ReporteMensual> findByTitularAndPeriod(UUID titularId, Integer mes, Integer anho);
}
