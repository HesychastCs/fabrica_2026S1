package com.example.reportes.application.usecase;

import java.util.UUID;

import com.example.reportes.domain.model.ReporteMensual;

public interface GetMonthlyReportUseCase {

    ReporteMensual getByPeriod(UUID titularId, Integer mes, Integer anho);

    ReporteMensual getById(UUID reporteId);
}
