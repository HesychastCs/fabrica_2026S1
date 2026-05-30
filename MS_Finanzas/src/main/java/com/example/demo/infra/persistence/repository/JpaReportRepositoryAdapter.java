package com.example.demo.infra.persistence.repository;
import org.springframework.stereotype.Component;

import com.example.demo.application.repository.ReportRepositoryPort;
import com.example.demo.domain.model.Report;
import com.example.demo.infra.mapper.ReportEntityMapper;
import com.example.demo.infra.persistence.entity.ReportEntity;

@Component
public class JpaReportRepositoryAdapter implements ReportRepositoryPort {
    private final JpaReportRepository jpaReportRepository;
    private final ReportEntityMapper reportEntityMapper;
    
    public JpaReportRepositoryAdapter(JpaReportRepository jpaReportRepository, ReportEntityMapper reportEntityMapper) {
        this.jpaReportRepository = jpaReportRepository;
        this.reportEntityMapper = reportEntityMapper;
    }

    @Override
    public Report save(Report report) {  
        ReportEntity reportEntity = reportEntityMapper.toEntity(report);
        ReportEntity savedReportEntity = jpaReportRepository.save(reportEntity);
        return reportEntityMapper.toDomain(savedReportEntity, savedReportEntity.getTitular());
    }
}
