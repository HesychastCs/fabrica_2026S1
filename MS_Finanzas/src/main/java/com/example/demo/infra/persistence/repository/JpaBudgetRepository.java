package com.example.demo.infra.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.infra.persistence.entity.BudgetEntity;

@Repository
public interface JpaBudgetRepository extends JpaRepository<BudgetEntity, UUID> {
    @Query("""
        SELECT b FROM BudgetEntity b
        WHERE b.titular.titularId = :titularId
            AND b.fechaInicio <= :fecha
            AND (b.fechaFinal IS NULL OR b.fechaFinal >= :fecha)
        """)
    List<BudgetEntity> findByTitularAndDateRange(
        @Param("titularId") UUID titularId,
        @Param("fecha") LocalDate fecha
    );
}
