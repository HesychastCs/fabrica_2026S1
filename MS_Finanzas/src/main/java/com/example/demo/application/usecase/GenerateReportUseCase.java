package com.example.demo.application.usecase;

import java.util.UUID;

import com.example.demo.domain.model.Report;

public interface GenerateReportUseCase {
    Report generateReport(Integer mes, Integer anho, UUID titularId);
}
