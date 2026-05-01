package com.example.demo.infra.persistence.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.infra.persistence.entity.ReportEntity;

@Repository
public interface JpaReportRepository extends JpaRepository<ReportEntity, UUID>{

}
