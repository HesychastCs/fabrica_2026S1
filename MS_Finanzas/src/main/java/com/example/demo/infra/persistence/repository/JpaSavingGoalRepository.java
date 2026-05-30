package com.example.demo.infra.persistence.repository;

import com.example.demo.infra.persistence.entity.SavingGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaSavingGoalRepository extends JpaRepository<SavingGoalEntity, UUID> {
    
    boolean existsByNombre(String nombre);
    
    // CORREGIDO: Usando @Query explícita
    @Query("SELECT s FROM SavingGoalEntity s WHERE s.titular.titularId = :titularId")
    List<SavingGoalEntity> findByTitularId(@Param("titularId") UUID titularId);
    
    // NUEVO: Consulta no trivial con JOIN y agregación
            @Query(value = """
        SELECT 
            m.meta_id as metaId,
            m.nombre as nombreMeta,
            m.monto_objetivo as montoObjetivo,
            m.avance as totalAportado,
            CASE 
                WHEN m.monto_objetivo > 0 
                THEN (m.avance / m.monto_objetivo) * 100 
                ELSE 0 
            END as porcentajeAvance,
            m.fecha_limite as fechaLimite,
            (m.fecha_limite - CURRENT_DATE) as diasRestantes,
            CASE 
                WHEN m.avance >= m.monto_objetivo 
                THEN CURRENT_DATE
                ELSE NULL
            END as fechaProyectadaCumplimiento
        FROM metas_ahorro m
        WHERE m.titular_id = :titularId
        ORDER BY m.fecha_limite ASC
        """, nativeQuery = true)
    List<Object[]> obtenerAvanceMetas(@Param("titularId") UUID titularId);
            }