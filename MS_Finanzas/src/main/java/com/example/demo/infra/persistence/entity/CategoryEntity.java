package com.example.demo.infra.persistence.entity;

import java.util.List;
import java.util.UUID;

import com.example.demo.domain.model.TypeCategory;

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
@Table(name = "categoria")
@Data
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(name="nombre", nullable=false)
    private String nombre;
        
    @Column(name="tipo", nullable=false)
    private TypeCategory tipo;

    @OneToMany(mappedBy="categoria", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private List<TransactionEntity> transacciones;
}
