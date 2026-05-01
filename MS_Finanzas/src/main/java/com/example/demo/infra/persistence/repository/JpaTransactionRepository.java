package com.example.demo.infra.persistence.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.persistence.entity.TransactionEntity;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("""
        SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
        FROM TransactionEntity t WHERE t.categoria.id = :categoryId
        """)
    boolean existsByCategoryId(@Param("categoryId") UUID categoryId);

    @Query("""
        SELECT t FROM TransactionEntity t
        WHERE (:tipo IS NULL OR t.tipo = :tipo)
          AND (:categoriaId IS NULL OR t.categoria.categoriaId = :categoriaId)
          AND (:desde IS NULL OR t.fecha >= :desde)
          AND (:hasta IS NULL OR t.fecha <= :hasta)
        ORDER BY t.fecha DESC, t.transactionId DESC
        """)
    List<TransactionEntity> findFiltered(
        @Param("tipo") TypeTransaction tipo,
        @Param("categoriaId") UUID categoriaId,
        @Param("desde") LocalDate desde,
        @Param("hasta") LocalDate hasta
    );

    @Query("""
        SELECT COALESCE(SUM(t.monto), 0)
        FROM TransactionEntity t
        WHERE t.titular.titularId = :titularId
            AND t.tipo = :tipo
            AND YEAR(t.fecha) = :anho
            AND MONTH(t.fecha) = :mes
        """)
    BigDecimal sumByTitularAndTypeAndMonth(
        @Param("titularId") UUID titularId,
        @Param("tipo") TypeTransaction tipo,
        @Param("anho") Integer anho,
        @Param("mes") Integer mes
    );

    @Query("""
        SELECT COALESCE(SUM(t.monto), 0)
        FROM TransactionEntity t
        WHERE t.titular.titularId = :titularId
            AND t.tipo = :tipo
        """)
    BigDecimal sumByTitularAndType(
        @Param("titularId") UUID titularId,
        @Param("tipo") TypeTransaction tipo
    );

    @Query("""
    SELECT COALESCE(SUM(
        CASE 
            WHEN t.tipo = 'ingreso' THEN t.monto
            WHEN t.tipo = 'retiro_meta' THEN t.monto
            WHEN t.tipo = 'gasto' THEN -t.monto
            WHEN t.tipo = 'aporte_meta' THEN -t.monto
            ELSE 0
        END
        ), 0)
        FROM TransactionEntity t
        WHERE t.titular.titularId = :titularId
            AND YEAR(t.fecha) = :anho
            AND MONTH(t.fecha) = :mes
        """)
    BigDecimal calculateNetBalanceByMonth(
        @Param("titularId") UUID titularId,
        @Param("anho") Integer anho,
        @Param("mes") Integer mes
    );
    @Query("""
        SELECT COALESCE(SUM(
            CASE 
                WHEN t.tipo = 'ingreso' THEN t.monto
                WHEN t.tipo = 'retiro_meta' THEN t.monto
                WHEN t.tipo = 'gasto' THEN -t.monto
                WHEN t.tipo = 'aporte_meta' THEN -t.monto
                ELSE 0
            END
        ), 0)
        FROM TransactionEntity t
        WHERE t.titular.titularId = :titularId
        """)
    BigDecimal calculateNetBalanceAllTime(
        @Param("titularId") UUID titularId
    );
}
