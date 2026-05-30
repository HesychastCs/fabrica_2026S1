package com.example.reportes.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.reportes.application.repository.FinanzasGatewayPort;
import com.example.reportes.application.repository.ReporteRepositoryPort;
import com.example.reportes.application.usecase.GenerateMonthlyReportUseCase;
import com.example.reportes.application.usecase.GetMonthlyReportUseCase;
import com.example.reportes.domain.exception.NoTransactionsInMonthException;
import com.example.reportes.domain.exception.ResourceNotFoundException;
import com.example.reportes.domain.model.FinanzasTransaction;
import com.example.reportes.domain.model.GastoPorCategoria;
import com.example.reportes.domain.model.MovimientoReporte;
import com.example.reportes.domain.model.ReportStatus;
import com.example.reportes.domain.model.ReporteMensual;
import com.example.reportes.domain.model.TransactionType;
import com.example.reportes.infra.mapper.ReporteEntityMapper;

@Service
public class MonthlyReportService implements GenerateMonthlyReportUseCase, GetMonthlyReportUseCase {

    private static final String CATEGORIA_VACIA = "Vacía";

    private final FinanzasGatewayPort finanzasGatewayPort;
    private final ReporteRepositoryPort reporteRepositoryPort;

    public MonthlyReportService(FinanzasGatewayPort finanzasGatewayPort, ReporteRepositoryPort reporteRepositoryPort) {
        this.finanzasGatewayPort = finanzasGatewayPort;
        this.reporteRepositoryPort = reporteRepositoryPort;
    }

    @Override
    public ReporteMensual generate(UUID titularId, Integer mes, Integer anho, String moneda) {
        finanzasGatewayPort.ensureTitularExists(titularId);
        List<FinanzasTransaction> transacciones = finanzasGatewayPort.listTransactions(titularId, mes, anho);

        BigDecimal ingresos = sumByType(transacciones, TransactionType.INGRESO);
        BigDecimal gastos = sumByType(transacciones, TransactionType.GASTO);
        BigDecimal aportes = sumByType(transacciones, TransactionType.APORTE_META);
        BigDecimal retiros = sumByType(transacciones, TransactionType.RETIRO_META);

        if (ingresos.signum() == 0 && gastos.signum() == 0 && aportes.signum() == 0 && retiros.signum() == 0) {
            throw new NoTransactionsInMonthException(mes, anho);
        }

        BigDecimal balance = ingresos.subtract(gastos).add(retiros).subtract(aportes);
        List<GastoPorCategoria> gastosPorCategoria = buildGastosPorCategoria(transacciones, gastos);
        List<MovimientoReporte> movimientos = transacciones.stream().map(this::toMovimiento).toList();

        String monedaFinal = moneda != null && !moneda.isBlank() ? moneda : "COP";

        ReporteMensual reporte = new ReporteMensual(
            null,
            titularId,
            mes,
            anho,
            ingresos,
            gastos,
            aportes,
            retiros,
            balance,
            monedaFinal,
            ReportStatus.GENERADO,
            Instant.now(),
            gastosPorCategoria,
            movimientos
        );

        return reporteRepositoryPort.save(reporte);
    }

    @Override
    public ReporteMensual getByPeriod(UUID titularId, Integer mes, Integer anho) {
        return reporteRepositoryPort.findByTitularAndPeriod(titularId, mes, anho)
            .orElseThrow(() -> new ResourceNotFoundException(
                "No existe reporte para el titular en el periodo " + mes + "/" + anho
            ));
    }

    @Override
    public ReporteMensual getById(UUID reporteId) {
        return reporteRepositoryPort.findById(reporteId)
            .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
    }

    private BigDecimal sumByType(List<FinanzasTransaction> transacciones, TransactionType tipo) {
        return transacciones.stream()
            .filter(t -> t.tipo() == tipo)
            .map(FinanzasTransaction::monto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<GastoPorCategoria> buildGastosPorCategoria(
        List<FinanzasTransaction> transacciones,
        BigDecimal gastosTotal
    ) {
        Map<String, BigDecimal> acumulado = new LinkedHashMap<>();
        for (FinanzasTransaction t : transacciones) {
            if (t.tipo() != TransactionType.GASTO) {
                continue;
            }
            String nombre = categoriaNombre(t.nombreCategoria());
            acumulado.merge(nombre, t.monto(), BigDecimal::add);
        }

        List<GastoPorCategoria> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : acumulado.entrySet()) {
            result.add(new GastoPorCategoria(
                null,
                entry.getKey(),
                entry.getValue(),
                ReporteEntityMapper.porcentaje(entry.getValue(), gastosTotal)
            ));
        }
        return result;
    }

    private MovimientoReporte toMovimiento(FinanzasTransaction t) {
        return new MovimientoReporte(
            t.transactionId(),
            t.tipo(),
            t.nombre(),
            t.descripcion(),
            t.monto(),
            t.fecha(),
            categoriaNombre(t.nombreCategoria())
        );
    }

    private String categoriaNombre(String nombre) {
        return nombre == null || nombre.isBlank() ? CATEGORIA_VACIA : nombre;
    }
}
