package com.example.demo.infra.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
@Table(name = "presupuestos")
@Data
public class BudgetEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="presupuesto_id")
    private UUID presupuestoId;

    @Column(name="monto_limite", nullable=false, precision=15, scale=2)
    private BigDecimal montoLimite;
    
    @Column(name="fecha_creacion", nullable=false)
    private Instant fechaCreacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_final", nullable = true)
    private LocalDate fechaFinal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="titular_id")
    private TitularEntity titular;
}
