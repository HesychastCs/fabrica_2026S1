package com.example.demo.infra.persistence.repository;
import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Component;

import com.example.demo.application.repository.ReportRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Report;
import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.mapper.ReportEntityMapper;
import com.example.demo.infra.mapper.TitularEntityMapper;
import com.example.demo.infra.persistence.entity.ReportEntity;
import com.example.demo.infra.persistence.entity.TitularEntity;

@Component
public class JpaReportRepositoryAdapter implements ReportRepositoryPort {
    private final JpaReportRepository jpaReportRepository;
    private final ReportEntityMapper reportEntityMapper;
    private final JpaTitularRepository jpaTitularRepository;
    private final JpaTransactionRepository jpaTransactionRepository;
    
    public JpaReportRepositoryAdapter(JpaReportRepository jpaReportRepository, ReportEntityMapper reportEntityMapper, JpaTitularRepository jpaTitularRepository, JpaTransactionRepository jpaTransactionRepository, TitularEntityMapper titularEntityMapper) {
        this.jpaReportRepository = jpaReportRepository;
        this.reportEntityMapper = reportEntityMapper;
        this.jpaTitularRepository = jpaTitularRepository;
        this.jpaTransactionRepository = jpaTransactionRepository;
    }

    @Override
    public Report save(Report report) {  // Note: rename from save()
        if (jpaTitularRepository.existsById(report.titular().titularId())) {
            throw new ResourceNotFoundException("El titular no fue identificado");
        }
        TitularEntity titularEntity = jpaTitularRepository.findById(report.titular().titularId()).orElseThrow(() -> new ResourceNotFoundException("El titular no fue identificado"));
            
        // Query transactions to calculate fields
        BigDecimal ingresosAcumulados = jpaTransactionRepository.sumByTitularAndType(report.titular().titularId(), TypeTransaction.ingreso);
        BigDecimal gastosAcumulados = jpaTransactionRepository.sumByTitularAndType(report.titular().titularId(), TypeTransaction.gasto);
        BigDecimal aportesMetaAcumulados = jpaTransactionRepository.sumByTitularAndType(report.titular().titularId(), TypeTransaction.aporte_meta);
        BigDecimal balanceNeto = jpaTransactionRepository.calculateNetBalanceAllTime(report.titular().titularId());
        
        Report reportWithValues = new Report(
            report.reportId(),
            report.mes(),
            report.anho(),
            ingresosAcumulados,
            gastosAcumulados,
            aportesMetaAcumulados,
            balanceNeto,
            Instant.now(),
            report.titular());
        ReportEntity reportEntity = reportEntityMapper.toEntity(reportWithValues);
        ReportEntity savedReportEntity = jpaReportRepository.save(reportEntity);
        return reportEntityMapper.toDomain(savedReportEntity, titularEntity);
    }
}
