package com.example.reportes.infra.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.reportes.domain.model.GastoPorCategoria;
import com.example.reportes.domain.model.MovimientoReporte;
import com.example.reportes.domain.model.ReportStatus;
import com.example.reportes.domain.model.ReporteMensual;
import com.example.reportes.domain.model.TransactionType;
import com.example.reportes.infra.persistence.entity.ReporteEntity;
import com.example.reportes.infra.persistence.entity.ReporteGastoCategoriaEntity;
import com.example.reportes.infra.persistence.entity.ReporteMovimientoEntity;

@Component
public class ReporteEntityMapper {

    public ReporteMensual toDomain(ReporteEntity entity) {
        List<GastoPorCategoria> gastos = entity.getGastosCategoria() == null
            ? List.of()
            : entity.getGastosCategoria().stream().map(this::toGastoDomain).toList();
        List<MovimientoReporte> movimientos = entity.getMovimientos() == null
            ? List.of()
            : entity.getMovimientos().stream().map(this::toMovimientoDomain).toList();

        return new ReporteMensual(
            entity.getReporteId(),
            entity.getTitularId(),
            entity.getMes(),
            entity.getAnho(),
            entity.getIngresosTotal(),
            entity.getGastosTotal(),
            entity.getAportesMetaTotal(),
            entity.getRetirosMetaTotal(),
            entity.getBalanceNeto(),
            entity.getMoneda(),
            ReportStatus.valueOf(entity.getEstado()),
            entity.getFechaGenerado(),
            gastos,
            movimientos
        );
    }

    public ReporteEntity toEntity(ReporteMensual domain) {
        ReporteEntity entity = new ReporteEntity();
        entity.setReporteId(domain.reporteId());
        entity.setTitularId(domain.titularId());
        entity.setMes(domain.mes());
        entity.setAnho(domain.anho());
        entity.setIngresosTotal(domain.ingresosTotal());
        entity.setGastosTotal(domain.gastosTotal());
        entity.setAportesMetaTotal(domain.aportesMetaTotal());
        entity.setRetirosMetaTotal(domain.retirosMetaTotal());
        entity.setBalanceNeto(domain.balanceNeto());
        entity.setMoneda(domain.moneda() != null ? domain.moneda() : "COP");
        entity.setEstado(domain.estado() != null ? domain.estado().name() : ReportStatus.GENERADO.name());
        entity.setFechaGenerado(domain.fechaGenerado());

        List<ReporteGastoCategoriaEntity> gastosEntities = new ArrayList<>();
        for (GastoPorCategoria gasto : domain.gastosPorCategoria()) {
            ReporteGastoCategoriaEntity g = new ReporteGastoCategoriaEntity();
            g.setReporte(entity);
            g.setCategoriaId(gasto.categoriaId());
            g.setCategoriaNombre(gasto.categoriaNombre());
            g.setMontoTotal(gasto.montoTotal());
            g.setPorcentajeDelTotal(gasto.porcentajeDelTotal());
            gastosEntities.add(g);
        }
        entity.setGastosCategoria(gastosEntities);

        List<ReporteMovimientoEntity> movimientoEntities = new ArrayList<>();
        for (MovimientoReporte mov : domain.movimientos()) {
            ReporteMovimientoEntity m = new ReporteMovimientoEntity();
            m.setReporte(entity);
            m.setTransaccionId(mov.transaccionId());
            m.setTipo(mov.tipo().name());
            m.setNombre(mov.nombre());
            m.setDescripcion(mov.descripcion());
            m.setMonto(mov.monto());
            m.setFechaPago(mov.fechaPago());
            m.setCategoriaNombre(mov.categoriaNombre());
            movimientoEntities.add(m);
        }
        entity.setMovimientos(movimientoEntities);
        return entity;
    }

    private GastoPorCategoria toGastoDomain(ReporteGastoCategoriaEntity entity) {
        return new GastoPorCategoria(
            entity.getCategoriaId(),
            entity.getCategoriaNombre(),
            entity.getMontoTotal(),
            entity.getPorcentajeDelTotal()
        );
    }

    private MovimientoReporte toMovimientoDomain(ReporteMovimientoEntity entity) {
        return new MovimientoReporte(
            entity.getTransaccionId(),
            TransactionType.valueOf(entity.getTipo()),
            entity.getNombre(),
            entity.getDescripcion(),
            entity.getMonto(),
            entity.getFechaPago(),
            entity.getCategoriaNombre()
        );
    }

    public static BigDecimal porcentaje(BigDecimal parte, BigDecimal total) {
        if (total == null || total.signum() == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return parte.multiply(BigDecimal.valueOf(100))
            .divide(total, 2, RoundingMode.HALF_UP);
    }
}
