package com.example.demo.infra.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.infra.persistence.entity.TitularEntity;

@Repository
public interface JpaTitularRepository extends JpaRepository<TitularEntity, UUID>{

}
