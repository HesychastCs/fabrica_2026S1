package com.example.reportes.infra.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reportes.application.service.MonthlyReportService;
import com.example.reportes.domain.model.ReporteMensual;
import com.example.reportes.infra.mapper.MonthlyReportResponseMapper;
import com.example.reportes.infra.rest.dto.GastoPorCategoriaResponse;
import com.example.reportes.infra.rest.dto.MonthlyReportRequest;
import com.example.reportes.infra.rest.dto.MonthlyReportResponse;
import com.example.reportes.infra.rest.dto.MovimientoReporteResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reports")
public class MonthlyReportController {

    private final MonthlyReportService monthlyReportService;
    private final MonthlyReportResponseMapper responseMapper;

    public MonthlyReportController(
        MonthlyReportService monthlyReportService,
        MonthlyReportResponseMapper responseMapper
    ) {
        this.monthlyReportService = monthlyReportService;
        this.responseMapper = responseMapper;
    }

    @PostMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> generate(@Valid @RequestBody MonthlyReportRequest request) {
        ReporteMensual reporte = monthlyReportService.generate(
            request.titularId(),
            request.mes(),
            request.anho(),
            request.moneda()
        );
        return ResponseEntity.ok(responseMapper.toResponse(reporte));
    }

    @GetMapping
    public ResponseEntity<MonthlyReportResponse> getByPeriod(
        @RequestParam UUID titularId,
        @RequestParam Integer mes,
        @RequestParam Integer anho
    ) {
        ReporteMensual reporte = monthlyReportService.getByPeriod(titularId, mes, anho);
        return ResponseEntity.ok(responseMapper.toResponse(reporte));
    }

    @GetMapping("/{reporteId}")
    public ResponseEntity<MonthlyReportResponse> getById(@PathVariable UUID reporteId) {
        ReporteMensual reporte = monthlyReportService.getById(reporteId);
        return ResponseEntity.ok(responseMapper.toResponse(reporte));
    }

    @GetMapping("/{reporteId}/gastos")
    public ResponseEntity<List<GastoPorCategoriaResponse>> getGastos(@PathVariable UUID reporteId) {
        ReporteMensual reporte = monthlyReportService.getById(reporteId);
        List<GastoPorCategoriaResponse> gastos = reporte.gastosPorCategoria().stream()
            .map(g -> new GastoPorCategoriaResponse(
                g.categoriaId(),
                g.categoriaNombre(),
                g.montoTotal(),
                g.porcentajeDelTotal()
            ))
            .toList();
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/{reporteId}/movimientos")
    public ResponseEntity<List<MovimientoReporteResponse>> getMovimientos(@PathVariable UUID reporteId) {
        ReporteMensual reporte = monthlyReportService.getById(reporteId);
        List<MovimientoReporteResponse> movimientos = reporte.movimientos().stream()
            .map(m -> new MovimientoReporteResponse(
                m.transaccionId(),
                m.tipo(),
                m.nombre(),
                m.descripcion(),
                m.monto(),
                m.fechaPago(),
                m.categoriaNombre()
            ))
            .toList();
        return ResponseEntity.ok(movimientos);
    }
}
