package com.example.demo.application.service;

import com.example.demo.application.repository.ReportRepositoryPort;
import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.domain.exception.NoTransactionsInMonthException;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Report;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.TypeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ReportService
 * Cubre HU-08 — Ver reporte financiero mensual
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

        @Mock
        private ReportRepositoryPort reportRepositoryPort;
        @Mock
        private TitularRepositoryPort titularRepositoryPort;
        @Mock
        private TransactionRepositoryPort transactionRepositoryPort;

        @InjectMocks
        private ReportService reportService;

        private Titular titular;
        private UUID titularId;

        // Mes y año de referencia para las pruebas
        private static final int MES = 3; // Marzo
        private static final int ANHO = 2026;

        @BeforeEach
        void setUp() {
                titularId = UUID.randomUUID();
                titular = new Titular(titularId, "Carlos", "Mora", "Vargas",
                                "3151234567", Instant.now(), "COP", "America/Bogota", "token-rep");
        }

        // ─────────────────────────────────────────────────────────────────
        // HU-08 — Ver reporte financiero mensual
        // ─────────────────────────────────────────────────────────────────
        @Nested
        @DisplayName("HU-08 — Ver reporte financiero mensual")
        class VerReporteMensual {

                /**
                 * CA-01 + CA-02 @happy-path
                 * Generar reporte de Marzo 2026 con datos → muestra ingresos, gastos,
                 * aportes a metas y balance neto coincidiendo con la suma real.
                 */
                @Test
                @DisplayName("CA-01 CA-02 — Reporte con datos muestra totales correctos y balance neto exacto")
                void ca01_ca02_reporteConDatosMuestraTotalesCorrectos() {
                        BigDecimal ingresos = BigDecimal.valueOf(5_000_000);
                        BigDecimal gastos = BigDecimal.valueOf(2_000_000);
                        BigDecimal aportes = BigDecimal.valueOf(500_000);
                        BigDecimal retiros = BigDecimal.ZERO;
                        // Balance = ingresos - gastos + retiros - aportes
                        BigDecimal balanceEsperado = ingresos.subtract(gastos).add(retiros).subtract(aportes);
                        // = 5.000.000 - 2.000.000 - 500.000 = 2.500.000

                        when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titular));
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId, TypeTransaction.INGRESO,
                                        ANHO, MES))
                                        .thenReturn(ingresos);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId, TypeTransaction.GASTO,
                                        ANHO, MES))
                                        .thenReturn(gastos);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId,
                                        TypeTransaction.APORTE_META, ANHO, MES))
                                        .thenReturn(aportes);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId,
                                        TypeTransaction.RETIRO_META, ANHO, MES))
                                        .thenReturn(retiros);

                        Report reportSaved = new Report(UUID.randomUUID(), MES, ANHO,
                                        ingresos, gastos, aportes.subtract(retiros), balanceEsperado, Instant.now(),
                                        titular);
                        when(reportRepositoryPort.save(any())).thenReturn(reportSaved);

                        Report result = reportService.generateReport(MES, ANHO, titularId);

                        assertThat(result).isNotNull();
                        assertThat(result.ingresosAcumulados()).isEqualByComparingTo(ingresos);
                        assertThat(result.gastosAcumulados()).isEqualByComparingTo(gastos);
                        assertThat(result.balanceNeto()).isEqualByComparingTo(balanceEsperado);
                        assertThat(result.mes()).isEqualTo(MES);
                        assertThat(result.anho()).isEqualTo(ANHO);
                }

                /**
                 * CA-03 @edge-case
                 * Sin movimientos en el mes → NoTransactionsInMonthException.
                 * El mensaje indica "No hay movimientos en este período".
                 */
                @Test
                @DisplayName("CA-03 — Sin movimientos en el mes lanza NoTransactionsInMonthException")
                void ca03_sinMovimientosLanzaExcepcion() {
                        int mesVacio = 4; // Abril 2025
                        int anhoVacio = 2025;

                        when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titular));
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId, TypeTransaction.INGRESO,
                                        anhoVacio, mesVacio))
                                        .thenReturn(null);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId, TypeTransaction.GASTO,
                                        anhoVacio, mesVacio))
                                        .thenReturn(null);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId,
                                        TypeTransaction.APORTE_META, anhoVacio, mesVacio))
                                        .thenReturn(null);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId,
                                        TypeTransaction.RETIRO_META, anhoVacio, mesVacio))
                                        .thenReturn(null);

                        assertThatThrownBy(() -> reportService.generateReport(mesVacio, anhoVacio, titularId))
                                        .isInstanceOf(NoTransactionsInMonthException.class);

                        verify(reportRepositoryPort, never()).save(any());
                }

                /**
                 * CA-04 @happy-path
                 * Balance negativo → gastos superan ingresos, el balance neto es negativo.
                 */
                @Test
                @DisplayName("CA-04 — Balance negativo cuando gastos superan ingresos")
                void ca04_balanceNegativoCuandoGastosSuperanIngresos() {
                        BigDecimal ingresos = BigDecimal.valueOf(1_000_000);
                        BigDecimal gastos = BigDecimal.valueOf(2_000_000); // gastos > ingresos
                        BigDecimal aportes = BigDecimal.ZERO;
                        BigDecimal retiros = BigDecimal.ZERO;
                        BigDecimal balanceEsperado = ingresos.subtract(gastos); // -1.000.000

                        when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titular));
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId, TypeTransaction.INGRESO,
                                        ANHO, MES))
                                        .thenReturn(ingresos);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId, TypeTransaction.GASTO,
                                        ANHO, MES))
                                        .thenReturn(gastos);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId,
                                        TypeTransaction.APORTE_META, ANHO, MES))
                                        .thenReturn(aportes);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId,
                                        TypeTransaction.RETIRO_META, ANHO, MES))
                                        .thenReturn(retiros);

                        Report reportSaved = new Report(UUID.randomUUID(), MES, ANHO,
                                        ingresos, gastos, BigDecimal.ZERO, balanceEsperado, Instant.now(), titular);
                        when(reportRepositoryPort.save(any())).thenReturn(reportSaved);

                        Report result = reportService.generateReport(MES, ANHO, titularId);

                        assertThat(result.balanceNeto()).isNegative();
                        assertThat(result.balanceNeto()).isEqualByComparingTo(BigDecimal.valueOf(-1_000_000));
                }

                /**
                 * Titular no encontrado → ResourceNotFoundException.
                 */
                @Test
                @DisplayName("Titular no encontrado lanza ResourceNotFoundException")
                void titularNoEncontradoLanzaExcepcion() {
                        when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> reportService.generateReport(MES, ANHO, titularId))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("titular");

                        verify(reportRepositoryPort, never()).save(any());
                }

                /**
                 * Cálculo exacto del balance neto incluyendo aportes y retiros de metas.
                 * balanceNeto = ingresos - gastos + retirosMeta - aportesMeta
                 */
                @Test
                @DisplayName("Balance neto correcto con aportes y retiros de metas incluidos")
                void balanceNetoIncluyeAportesYRetirosDeMetas() {
                        BigDecimal ingresos = BigDecimal.valueOf(4_000_000);
                        BigDecimal gastos = BigDecimal.valueOf(1_500_000);
                        BigDecimal aportes = BigDecimal.valueOf(800_000);
                        BigDecimal retiros = BigDecimal.valueOf(200_000);
                        // Balance = 4.000.000 - 1.500.000 + 200.000 - 800.000 = 1.900.000
                        BigDecimal balanceEsperado = BigDecimal.valueOf(1_900_000);

                        when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titular));
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId, TypeTransaction.INGRESO,
                                        ANHO, MES))
                                        .thenReturn(ingresos);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId, TypeTransaction.GASTO,
                                        ANHO, MES))
                                        .thenReturn(gastos);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId,
                                        TypeTransaction.APORTE_META, ANHO, MES))
                                        .thenReturn(aportes);
                        when(transactionRepositoryPort.sumByTitularAndTypeAndMonth(titularId,
                                        TypeTransaction.RETIRO_META, ANHO, MES))
                                        .thenReturn(retiros);

                        Report reportSaved = new Report(UUID.randomUUID(), MES, ANHO,
                                        ingresos, gastos, aportes.subtract(retiros), balanceEsperado, Instant.now(),
                                        titular);
                        when(reportRepositoryPort.save(any())).thenReturn(reportSaved);

                        Report result = reportService.generateReport(MES, ANHO, titularId);

                        assertThat(result.balanceNeto()).isEqualByComparingTo(balanceEsperado);
                }
        }
}
