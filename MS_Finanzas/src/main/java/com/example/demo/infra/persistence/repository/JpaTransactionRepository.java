package com.example.demo.infra.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.infra.persistence.entity.TransactionEntity;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID>{
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TransactionEntity t WHERE t.categoria.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") UUID categoryId);
}
