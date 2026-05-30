package com.example.reportes.infra.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reportes", schema = "reportes")
@Data
public class ReporteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reporte_id")
    private UUID reporteId;

    @Column(name = "titular_id", nullable = false)
    private UUID titularId;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anho;

    @Column(name = "ingresos_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal ingresosTotal = BigDecimal.ZERO;

    @Column(name = "gastos_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal gastosTotal = BigDecimal.ZERO;

    @Column(name = "aportes_meta_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal aportesMetaTotal = BigDecimal.ZERO;

    @Column(name = "retiros_meta_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal retirosMetaTotal = BigDecimal.ZERO;

    @Column(name = "balance_neto", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceNeto = BigDecimal.ZERO;

    @Column(nullable = false, length = 10)
    private String moneda = "COP";

    @Column(nullable = false, length = 20)
    private String estado = "GENERADO";

    @Column(name = "fecha_generado", nullable = false)
    private Instant fechaGenerado = Instant.now();

    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReporteGastoCategoriaEntity> gastosCategoria = new ArrayList<>();

    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReporteMovimientoEntity> movimientos = new ArrayList<>();
}
