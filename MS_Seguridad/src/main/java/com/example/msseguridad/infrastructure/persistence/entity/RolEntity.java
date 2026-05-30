package com.example.msseguridad.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "roles_seguridad")
public class RolEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String nombre;

    public RolEntity() {}

    public RolEntity(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}