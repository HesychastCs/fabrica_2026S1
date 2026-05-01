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
    public Report save(Report report) {  // Note: rename from save()
        // if (!jpaTitularRepository.existsById(titularId)) {
        //     throw new ResourceNotFoundException("El titular no fue identificado");
        // }
        // TitularEntity titularEntity = jpaTitularRepository.findById(titularId).orElseThrow(() -> new ResourceNotFoundException("El titular no fue identificado"));
            
        // // Query transactions to calculate fields
        // BigDecimal ingresosAcumulados = jpaTransactionRepository.sumByTitularAndType(titularId, TypeTransaction.ingreso);
        // BigDecimal gastosAcumulados = jpaTransactionRepository.sumByTitularAndType(titularId, TypeTransaction.gasto);
        // BigDecimal aportesMetaAcumulados = jpaTransactionRepository.sumByTitularAndType(titularId, TypeTransaction.aporte_meta);
        // BigDecimal balanceNeto = jpaTransactionRepository.calculateNetBalanceAllTime(titularId);
        
        // Report reportWithValues = new Report(
        //     null,
        //     mes,
        //     anho,
        //     ingresosAcumulados,
        //     gastosAcumulados,
        //     aportesMetaAcumulados,
        //     balanceNeto,
        //     Instant.now(),
        //     titularEntityMapper.toDomain(titularEntity)
        // );
        ReportEntity reportEntity = reportEntityMapper.toEntity(report);
        ReportEntity savedReportEntity = jpaReportRepository.save(reportEntity);
        return reportEntityMapper.toDomain(savedReportEntity, savedReportEntity.getTitular());
    }
}
