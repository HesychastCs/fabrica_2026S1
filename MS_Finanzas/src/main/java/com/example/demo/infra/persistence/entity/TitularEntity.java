package com.example.demo.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Entity
@Table(name = "titulares_financieros")
@Data
public class TitularEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="titular_id")
    private UUID titularId;

    @Column(name="nombre", nullable=false)
    @NotEmpty(message = "Name cannot be empty")
    private String nombre;

    @Column(name="primer_apellido")
    private String primerApellido;

    @Column(name="segundo_apellido")
    private String segundoApellido;
    
    @Column(name="telefono")
    private String telefono;

    @Column(name="token", nullable=false)
    private String token;

    @Column(name="zona_horaria", nullable=false)
    private String zonaHoraria;

    @Column(name="moneda_preferida", nullable=false)
    private String monedaPreferida;

    @Column(name="fecha_registro", nullable=false)
    private Instant fechaRegistro;
}