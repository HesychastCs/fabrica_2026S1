package com.example.reportes.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "auditoria_reporte", schema = "reportes")
@Data
public class AuditoriaReporteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "auditoria_id")
    private UUID auditoriaId;

    @Column(name = "reporte_id")
    private UUID reporteId;

    @Column(name = "titular_id")
    private UUID titularId;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column
    private String detalle;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
