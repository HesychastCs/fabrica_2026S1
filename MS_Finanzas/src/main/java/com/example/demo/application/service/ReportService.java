package com.example.demo.application.service;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.ReportRepositoryPort;
import com.example.demo.application.usecase.GenerateReportUseCase;
import com.example.demo.domain.model.Report;

@Service
public class ReportService implements GenerateReportUseCase{
    ReportRepositoryPort reportRepositoryPort;
    public ReportService(ReportRepositoryPort reportRepositoryPort) {
        this.reportRepositoryPort = reportRepositoryPort;
    }

    @Override
    public Report generateReport(Report report) {
        return reportRepositoryPort.save(report);
    }
}
