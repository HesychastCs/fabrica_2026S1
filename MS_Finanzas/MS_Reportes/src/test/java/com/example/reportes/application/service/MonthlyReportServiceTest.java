package com.example.reportes.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.reportes.application.repository.FinanzasGatewayPort;
import com.example.reportes.application.repository.ReporteRepositoryPort;
import com.example.reportes.domain.exception.NoTransactionsInMonthException;
import com.example.reportes.domain.model.FinanzasTransaction;
import com.example.reportes.domain.model.ReportStatus;
import com.example.reportes.domain.model.ReporteMensual;
import com.example.reportes.domain.model.TransactionType;

@ExtendWith(MockitoExtension.class)
class MonthlyReportServiceTest {

    @Mock
    private FinanzasGatewayPort finanzasGatewayPort;

    @Mock
    private ReporteRepositoryPort reporteRepositoryPort;

    @InjectMocks
    private MonthlyReportService monthlyReportService;

    @Test
    void generate_shouldPersistReportWhenTransactionsExist() {
        UUID titularId = UUID.randomUUID();
        List<FinanzasTransaction> transacciones = List.of(
            new FinanzasTransaction(
                UUID.randomUUID(), "Salario", BigDecimal.valueOf(1000),
                "", TransactionType.INGRESO, LocalDate.of(2026, 5, 10), null
            ),
            new FinanzasTransaction(
                UUID.randomUUID(), "Comida", BigDecimal.valueOf(200),
                "", TransactionType.GASTO, LocalDate.of(2026, 5, 12), "Alimentación"
            )
        );

        given(finanzasGatewayPort.listTransactions(titularId, 5, 2026)).willReturn(transacciones);
        given(reporteRepositoryPort.save(any(ReporteMensual.class))).willAnswer(inv -> {
            ReporteMensual r = inv.getArgument(0);
            return new ReporteMensual(
                UUID.randomUUID(), r.titularId(), r.mes(), r.anho(),
                r.ingresosTotal(), r.gastosTotal(), r.aportesMetaTotal(), r.retirosMetaTotal(),
                r.balanceNeto(), r.moneda(), r.estado(), r.fechaGenerado(),
                r.gastosPorCategoria(), r.movimientos()
            );
        });

        ReporteMensual result = monthlyReportService.generate(titularId, 5, 2026, "COP");

        assertEquals(BigDecimal.valueOf(1000), result.ingresosTotal());
        assertEquals(BigDecimal.valueOf(200), result.gastosTotal());
        assertEquals(BigDecimal.valueOf(800), result.balanceNeto());
        assertEquals(ReportStatus.GENERADO, result.estado());
        verify(finanzasGatewayPort).ensureTitularExists(titularId);
        verify(reporteRepositoryPort).save(any(ReporteMensual.class));
    }

    @Test
    void generate_shouldThrowWhenNoTransactions() {
        UUID titularId = UUID.randomUUID();
        given(finanzasGatewayPort.listTransactions(titularId, 5, 2026)).willReturn(List.of());

        assertThrows(
            NoTransactionsInMonthException.class,
            () -> monthlyReportService.generate(titularId, 5, 2026, "COP")
        );
    }
}
