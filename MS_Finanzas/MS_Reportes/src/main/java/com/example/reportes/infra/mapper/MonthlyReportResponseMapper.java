package com.example.reportes.infra.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.reportes.domain.model.GastoPorCategoria;
import com.example.reportes.domain.model.MovimientoReporte;
import com.example.reportes.domain.model.ReporteMensual;
import com.example.reportes.infra.rest.dto.GastoPorCategoriaResponse;
import com.example.reportes.infra.rest.dto.MonthlyReportResponse;
import com.example.reportes.infra.rest.dto.MovimientoReporteResponse;

@Component
public class MonthlyReportResponseMapper {

    public MonthlyReportResponse toResponse(ReporteMensual reporte) {
        return new MonthlyReportResponse(
            reporte.reporteId(),
            reporte.titularId(),
            reporte.mes(),
            reporte.anho(),
            reporte.ingresosTotal(),
            reporte.gastosTotal(),
            reporte.aportesMetaTotal(),
            reporte.retirosMetaTotal(),
            reporte.balanceNeto(),
            reporte.moneda(),
            reporte.estado(),
            reporte.fechaGenerado(),
            toGastosResponse(reporte.gastosPorCategoria()),
            toMovimientosResponse(reporte.movimientos())
        );
    }

    private List<GastoPorCategoriaResponse> toGastosResponse(List<GastoPorCategoria> gastos) {
        return gastos.stream()
            .map(g -> new GastoPorCategoriaResponse(
                g.categoriaId(),
                g.categoriaNombre(),
                g.montoTotal(),
                g.porcentajeDelTotal()
            ))
            .toList();
    }

    private List<MovimientoReporteResponse> toMovimientosResponse(List<MovimientoReporte> movimientos) {
        return movimientos.stream()
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
    }
}
