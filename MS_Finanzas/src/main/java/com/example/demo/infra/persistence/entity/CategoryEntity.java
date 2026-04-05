package com.example.demo.infra.persistence.entity;

import java.util.List;

import com.example.demo.domain.model.Transaction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "categorias")
@Data
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre", nullable=false)
    private String nombre;

    @OneToMany(mappedBy="categorias", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private List<Transaction> transacciones;
}
