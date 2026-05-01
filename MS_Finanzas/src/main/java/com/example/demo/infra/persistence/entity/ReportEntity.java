package com.example.demo.infra.persistence.entity;

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
@Table(name = "reportes")
@Data
public class ReportEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="reporte_id")
    private UUID reportId;
    
    @Column(nullable=false)
    private Integer mes;

    @Column(nullable=false)
    private Integer anho;

    @Column(name="ingresos_acmds", nullable=false, scale=2)
    private BigDecimal ingresosAcumulados;

    @Column(name="gastos_acmds", nullable=false, scale=2)
    private BigDecimal gastosAcumulados;
    
    @Column(name="aportes_ahorros_acmds", nullable=false, scale=2)
    private BigDecimal aportesMetaAcumulados;
    
    @Column(name="balance_neto", nullable=false, scale=2)
    private BigDecimal balanceNeto;

    @Column(name="fecha_generado", nullable=false)
    private Instant fechaGenerado;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="titular_id", nullable=false)
    private TitularEntity titular;

}
