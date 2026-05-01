package com.example.demo.infra.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.service.ReportService;
import com.example.demo.domain.model.Report;
import com.example.demo.infra.mapper.ReportRequestMapper;
import com.example.demo.infra.mapper.ReportResponseMapper;
import com.example.demo.infra.rest.dto.ReportRequest;
import com.example.demo.infra.rest.dto.ReportResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    private final ReportResponseMapper reportResponseMapper;
    private final ReportRequestMapper reportRequestMapper;
    public ReportController(ReportService reportService, ReportResponseMapper reportResponseMapper, ReportRequestMapper reportRequestMapper) {
        this.reportService = reportService;
        this.reportResponseMapper = reportResponseMapper;
        this.reportRequestMapper = reportRequestMapper;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> generateReport(@Valid @RequestBody ReportRequest reportRequest) {
        Report report = reportRequestMapper.toDomain(reportRequest);
        Report generatedReport = reportService.generateReport(report);
        return ResponseEntity.ok(reportResponseMapper.toResponse(generatedReport));

    }

}
