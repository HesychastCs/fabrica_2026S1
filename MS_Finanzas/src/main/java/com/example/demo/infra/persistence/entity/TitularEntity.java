package com.example.demo.infra.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "titulares_financieros")
@Data
public class TitularEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID titularId;

    @Column(name="nombre", nullable=false)
    @NotBlank
    private String nombre;
}
