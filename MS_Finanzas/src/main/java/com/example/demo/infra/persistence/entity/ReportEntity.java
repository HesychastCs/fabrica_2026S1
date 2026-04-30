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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

@Entity
@Table(name = "reportes")
@Data
public class ReportEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID reportId;
    
    @Column(nullable=false)
    private Integer mes;

    @Column(nullable=false)
    private Integer anho;

    @Column(nullable=false, scale=2)
    @Digits(integer=15, fraction=2)
    @DecimalMin(value="0.00", inclusive=true)
    private BigDecimal ingresosAcumulados;

    @Column(nullable=false, scale=2)
    @Digits(integer=15, fraction=2)
    @DecimalMin(value="0.00", inclusive=true)
    private BigDecimal gastosAcumulados;
    
    @Column(nullable=false, scale=2)
    @Digits(integer=15, fraction=2)
    @DecimalMin(value="0.00", inclusive=true)
    private BigDecimal aportesMetaAcumulados;
    
    @Column(nullable=false, scale=2)
    @Digits(integer=15, fraction=2)
    private BigDecimal balanceNeto;

    @Column(nullable=false)
    @PastOrPresent(message="La fecha de generación no puede ser futura")
    private Instant fechaGenerado;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="titular_id", nullable=false)
    private UUID titularId;

}
