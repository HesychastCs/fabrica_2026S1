package com.example.demo.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.ReportRepositoryPort;
import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.application.usecase.GenerateReportUseCase;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Report;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.TypeTransaction;

@Service
public class ReportService implements GenerateReportUseCase{
    private final ReportRepositoryPort reportRepositoryPort;
    private final TitularRepositoryPort titularRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    
    public ReportService(ReportRepositoryPort reportRepositoryPort, TitularRepositoryPort titularRepositoryPort, TransactionRepositoryPort transactionRepositoryPort) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.titularRepositoryPort = titularRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public Report generateReport(Integer mes, Integer anho, UUID titularId) {
        // Validate titular exists
        Titular titular = titularRepositoryPort.findById(titularId)
            .orElseThrow(() -> new ResourceNotFoundException("El titular no fue identificado"));
        
        // Calculate aggregates via port
        BigDecimal ingresosAcumulados = nullToZero(transactionRepositoryPort.sumByTitularAndType(titularId, TypeTransaction.INGRESO));
        BigDecimal gastosAcumulados = nullToZero(transactionRepositoryPort.sumByTitularAndType(titularId, TypeTransaction.GASTO));
        BigDecimal aportesMetaAcumulados = nullToZero(transactionRepositoryPort.sumByTitularAndType(titularId, TypeTransaction.APORTE_META));
        BigDecimal retirosMetaAcumulados = nullToZero(transactionRepositoryPort.sumByTitularAndType(titularId, TypeTransaction.RETIRO_META));
        BigDecimal balanceNeto = nullToZero(transactionRepositoryPort.calculateNetBalanceAllTime(titularId));
        
        Report report = new Report(null, mes, anho, ingresosAcumulados, gastosAcumulados, 
            aportesMetaAcumulados.subtract(retirosMetaAcumulados), balanceNeto, Instant.now(), titular);
        
        return reportRepositoryPort.save(report);   
    }
    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
    
