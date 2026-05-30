package com.example.reportes.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.reportes.application.repository.ReporteRepositoryPort;
import com.example.reportes.domain.model.ReporteMensual;
import com.example.reportes.infra.mapper.ReporteEntityMapper;
import com.example.reportes.infra.persistence.entity.AuditoriaReporteEntity;
import com.example.reportes.infra.persistence.entity.ReporteEntity;

@Component
public class JpaReporteRepositoryAdapter implements ReporteRepositoryPort {

    private final JpaReporteRepository jpaReporteRepository;
    private final JpaAuditoriaReporteRepository jpaAuditoriaReporteRepository;
    private final ReporteEntityMapper reporteEntityMapper;

    public JpaReporteRepositoryAdapter(
        JpaReporteRepository jpaReporteRepository,
        JpaAuditoriaReporteRepository jpaAuditoriaReporteRepository,
        ReporteEntityMapper reporteEntityMapper
    ) {
        this.jpaReporteRepository = jpaReporteRepository;
        this.jpaAuditoriaReporteRepository = jpaAuditoriaReporteRepository;
        this.reporteEntityMapper = reporteEntityMapper;
    }

    @Override
    @Transactional
    public ReporteMensual save(ReporteMensual reporte) {
        ReporteEntity entity = jpaReporteRepository
            .findByTitularIdAndMesAndAnho(reporte.titularId(), reporte.mes(), reporte.anho())
            .orElseGet(ReporteEntity::new);

        if (entity.getReporteId() != null) {
            entity.getGastosCategoria().clear();
            entity.getMovimientos().clear();
        }

        ReporteEntity mapped = reporteEntityMapper.toEntity(reporte);
        entity.setTitularId(mapped.getTitularId());
        entity.setMes(mapped.getMes());
        entity.setAnho(mapped.getAnho());
        entity.setIngresosTotal(mapped.getIngresosTotal());
        entity.setGastosTotal(mapped.getGastosTotal());
        entity.setAportesMetaTotal(mapped.getAportesMetaTotal());
        entity.setRetirosMetaTotal(mapped.getRetirosMetaTotal());
        entity.setBalanceNeto(mapped.getBalanceNeto());
        entity.setMoneda(mapped.getMoneda());
        entity.setEstado(mapped.getEstado());
        entity.setFechaGenerado(mapped.getFechaGenerado());

        for (var g : mapped.getGastosCategoria()) {
            g.setReporte(entity);
            entity.getGastosCategoria().add(g);
        }
        for (var m : mapped.getMovimientos()) {
            m.setReporte(entity);
            entity.getMovimientos().add(m);
        }

        ReporteEntity saved = jpaReporteRepository.save(entity);

        AuditoriaReporteEntity auditoria = new AuditoriaReporteEntity();
        auditoria.setReporteId(saved.getReporteId());
        auditoria.setTitularId(saved.getTitularId());
        auditoria.setAccion("GENERAR");
        auditoria.setDetalle("Reporte %s/%s titular %s".formatted(
            saved.getMes(), saved.getAnho(), saved.getTitularId()
        ));
        jpaAuditoriaReporteRepository.save(auditoria);

        return reporteEntityMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteMensual> findById(UUID reporteId) {
        return jpaReporteRepository.findDetailedByReporteId(reporteId)
            .map(reporteEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteMensual> findByTitularAndPeriod(UUID titularId, Integer mes, Integer anho) {
        return jpaReporteRepository.findDetailedByTitularIdAndMesAndAnho(titularId, mes, anho)
            .map(reporteEntityMapper::toDomain);
    }
}
