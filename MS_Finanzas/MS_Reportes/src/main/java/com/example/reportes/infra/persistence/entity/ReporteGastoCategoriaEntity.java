package com.example.reportes.infra.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reporte_gasto_categoria", schema = "reportes")
@Data
public class ReporteGastoCategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporte_id", nullable = false)
    private ReporteEntity reporte;

    @Column(name = "categoria_id")
    private UUID categoriaId;

    @Column(name = "categoria_nombre", nullable = false)
    private String categoriaNombre;

    @Column(name = "monto_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "porcentaje_del_total", precision = 5, scale = 2)
    private BigDecimal porcentajeDelTotal;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
