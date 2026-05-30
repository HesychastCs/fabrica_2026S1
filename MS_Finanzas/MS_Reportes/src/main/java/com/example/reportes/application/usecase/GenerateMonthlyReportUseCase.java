package com.example.reportes.application.usecase;

import java.util.UUID;

import com.example.reportes.domain.model.ReporteMensual;

public interface GenerateMonthlyReportUseCase {

    ReporteMensual generate(UUID titularId, Integer mes, Integer anho, String moneda);
}
