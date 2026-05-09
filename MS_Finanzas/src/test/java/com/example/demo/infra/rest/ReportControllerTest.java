package com.example.demo.infra.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.example.demo.application.service.ReportService;
import com.example.demo.domain.model.Report;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.ReportRequestMapper;
import com.example.demo.infra.mapper.ReportResponseMapper;
import com.example.demo.infra.rest.dto.ReportRequest;
import com.example.demo.infra.rest.dto.ReportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ReportControllerTest {

    private final StubReportService reportService = new StubReportService();

    private ReportController controller;
    private ReportResponseMapper reportResponseMapper;
    private ReportRequestMapper reportRequestMapper;

    private UUID titularId;
    private ReportRequest request;
    private Report report;
    private ReportResponse response;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        request = new ReportRequest(5, 2026, titularId);
        report = new Report(UUID.randomUUID(), 5, 2026, BigDecimal.valueOf(1000000), BigDecimal.valueOf(500000), BigDecimal.valueOf(200000), BigDecimal.valueOf(300000), Instant.now(), new Titular(titularId, "Ana", "Lopez", "Garcia", "3001234567", Instant.now(), "COP", "America/Bogota", "token-1"));
        response = new ReportResponse(report.reportId(), report.mes(), report.anho(), report.ingresosAcumulados(), report.gastosAcumulados(), report.aportesMetaAcumulados(), report.balanceNeto(), report.fechaGenerado());
        reportResponseMapper = value -> response;
        reportRequestMapper = requestValue -> report;
        reportService.generatedReport = report;
        controller = new ReportController(reportService, reportResponseMapper, reportRequestMapper);
    }

    @Test
    void generateReport_shouldReturnOkWithReportResponse() {
        ResponseEntity<ReportResponse> result = controller.generateReport(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(reportService.lastMes).isEqualTo(report.mes());
        assertThat(reportService.lastAnho).isEqualTo(report.anho());
        assertThat(reportService.lastTitularId).isEqualTo(titularId);
    }

    private static final class StubReportService extends ReportService {

        private Report generatedReport;
        private Integer lastMes;
        private Integer lastAnho;
        private UUID lastTitularId;

        private StubReportService() {
            super(null, null, null);
        }

        @Override
        public Report generateReport(Integer mes, Integer anho, UUID titularId) {
            lastMes = mes;
            lastAnho = anho;
            lastTitularId = titularId;
            return generatedReport;
        }
    }
}
