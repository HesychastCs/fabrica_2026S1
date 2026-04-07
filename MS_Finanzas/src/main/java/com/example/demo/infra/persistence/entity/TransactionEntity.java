package com.example.demo.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.domain.model.TypeTransaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transaccion")
@Data
public final class TransactionEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(name="nombre", nullable=false)
    private String nombre;

    @Column(name="descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo", nullable=false)
    private TypeTransaction tipo;

    @CreationTimestamp
    @Column(name="fecha_creacion", nullable=false)
    private Instant fechaCreacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="categoria_id")
    private CategoryEntity categoria;
}
