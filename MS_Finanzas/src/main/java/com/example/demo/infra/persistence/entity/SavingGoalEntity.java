package com.example.demo.infra.persistence.entity;

import com.example.demo.domain.model.GoalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "metas_ahorro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingGoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "meta_id")
    private UUID goalId;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "monto_objetivo", nullable = false)
    private Double montoObjetivo;

    @Column(name = "avance", nullable = false)
    private Integer avance = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private GoalStatus estado;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "titular_id", nullable = false)
    private TitularEntity titular;
}