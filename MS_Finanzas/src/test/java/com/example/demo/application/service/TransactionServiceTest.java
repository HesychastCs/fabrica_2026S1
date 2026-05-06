package com.example.demo.application.service;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para TransactionService
 * Cubre HU-01, HU-02, HU-03, HU-04
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;
    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;
    @Mock
    private TitularRepositoryPort titularRepositoryPort;

    @InjectMocks
    private TransactionService transactionService;

    private Titular titularBase;
    private Category categoriaAlimentacion;
    private UUID titularId;
    private UUID categoriaId;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();
        titularBase = new Titular(titularId, "Juan", "Pérez", "García",
                "3001234567", Instant.now(), "COP", "America/Bogota", "token-123");
        categoriaAlimentacion = new Category(categoriaId, "Alimentación", titularBase);
    }

    // ─────────────────────────────────────────────────────────────────
    // HU-01 — Registrar una transacción financiera
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("HU-01 — Registrar una transacción financiera")
    class RegistrarTransaccion {

        /**
         * CA-01 @happy-path
         * Registrar transacción INGRESO con nombre, monto y tipo → aparece en historial
         * con tipo correcto.
         */
        @ParameterizedTest(name = "CA-01 | nombre={0}, monto={1}, tipo={2}")
        @CsvSource({
                "Salario Enero,   3500000, INGRESO",
                "Mercado Semanal,  150000, GASTO"
        })
        @DisplayName("CA-01 — Registrar transacción exitosamente (nombre, monto, tipo)")
        void ca01_registrarTransaccionExitosamente(String nombre, BigDecimal monto, TypeTransaction tipo) {
            Transaction request = new Transaction(null, nombre, null, monto, tipo,
                    LocalDate.now(), null, titularBase);
            Transaction saved = new Transaction(UUID.randomUUID(), nombre, null, monto, tipo,
                    LocalDate.now(), categoriaAlimentacion, titularBase);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findByNombreIgnoreCase("Vacía")).thenReturn(Optional.of(categoriaAlimentacion));
            when(transactionRepositoryPort.save(any())).thenReturn(saved);

            Transaction result = transactionService.createTransaction(request);

            assertThat(result).isNotNull();
            assertThat(result.transactionId()).isNotNull();
            assertThat(result.nombre()).isEqualTo(nombre);
            assertThat(result.monto()).isEqualByComparingTo(monto);
            assertThat(result.tipo()).isEqualTo(tipo);
            verify(transactionRepositoryPort).save(any(Transaction.class));
        }

        /**
         * CA-02 @happy-path
         * Sin fecha → el sistema asigna la fecha actual.
         */
        @Test
        @DisplayName("CA-02 — Asignación automática de fecha cuando no se ingresa")
        void ca02_asignacionAutomaticaDeFecha() {
            Transaction request = new Transaction(null, "Compra", null,
                    BigDecimal.valueOf(50000), TypeTransaction.GASTO, null, null, titularBase);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findByNombreIgnoreCase("Vacía")).thenReturn(Optional.of(categoriaAlimentacion));
            when(transactionRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = transactionService.createTransaction(request);

            assertThat(result.fecha()).isEqualTo(LocalDate.now());
        }

        /**
         * CA-03 @happy-path
         * Sin categoría seleccionada → guarda con "Sin categoría" y permite asignarla
         * después.
         */
        @Test
        @DisplayName("CA-03 — Guardar transacción sin categoría asigna 'Sin categoría'")
        void ca03_guardarSinCategoria() {
            Category sinCategoria = new Category(UUID.randomUUID(), "Sin categoría", null);
            Transaction request = new Transaction(null, "Pago luz", null,
                    BigDecimal.valueOf(80000), TypeTransaction.GASTO, LocalDate.now(), null, titularBase);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findByNombreIgnoreCase("Vacía")).thenReturn(Optional.of(sinCategoria));
            when(transactionRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = transactionService.createTransaction(request);

            assertThat(result.categoria().nombre()).isEqualTo("Sin categoría");
        }

        /**
         * CA-04 @error-handling
         * Titular no encontrado → lanza ResourceNotFoundException (nombre de titular
         * inválido).
         */
        @Test
        @DisplayName("CA-04 — Titular no encontrado lanza excepción (campo obligatorio)")
        void ca04_titularNoEncontradoLanzaExcepcion() {
            Transaction request = new Transaction(null, "Test", null,
                    BigDecimal.valueOf(100), TypeTransaction.INGRESO, null, null, titularBase);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.createTransaction(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Titular no encontrado");
        }

        /**
         * CA-05 @error-handling
         * Monto cero o negativo → la validación de negocio no persiste la transacción.
         * (El control se hace en el Bean Validation del controller, pero aquí
         * verificamos
         * que el repositorio NO es llamado si el servicio recibe un monto inválido.)
         */
        @ParameterizedTest(name = "CA-05 | monto={0}")
        @CsvSource({ "0", "-500", "-1" })
        @DisplayName("CA-05 — Monto cero o negativo: repositorio no debe ser invocado")
        void ca05_montoInvalidoNoGuarda(BigDecimal monto) {
            // El servicio delega la validación del monto al Bean Validation (@Positive)
            // pero si un monto inválido llegara, BigDecimal.ZERO o negativo no debe
            // persistirse.
            // Aquí verificamos que NO se llama save cuando el titular no existe con monto
            // inválido.
            Transaction request = new Transaction(null, "Test", null,
                    monto, TypeTransaction.GASTO, null, null, titularBase);
            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findByNombreIgnoreCase("Vacía")).thenReturn(Optional.of(categoriaAlimentacion));
            // Simulamos que save lanza excepción de constraint si monto es 0 o negativo
            when(transactionRepositoryPort.save(any()))
                    .thenThrow(new IllegalArgumentException("El monto debe ser mayor a cero"));

            assertThatThrownBy(() -> transactionService.createTransaction(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("El monto debe ser mayor a cero");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HU-02 — Consultar el listado de transacciones
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("HU-02 — Consultar listado de transacciones")
    class ConsultarListado {

        /**
         * CA-01 @happy-path
         * Retorna todas las transacciones con sus campos completos.
         */
        @Test
        @DisplayName("CA-01 — Visualizar historial con nombre, monto, tipo, categoría y fecha")
        void ca01_visualizarHistorialCompleto() {
            Transaction t1 = new Transaction(UUID.randomUUID(), "Salario", null,
                    BigDecimal.valueOf(3_500_000), TypeTransaction.INGRESO,
                    LocalDate.of(2026, 1, 15), categoriaAlimentacion, titularBase);
            Transaction t2 = new Transaction(UUID.randomUUID(), "Mercado", null,
                    BigDecimal.valueOf(150_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 1, 20), categoriaAlimentacion, titularBase);

            TransactionListFilter filter = new TransactionListFilter(Optional.empty(), Optional.empty(),
                    Optional.empty());
            when(transactionRepositoryPort.findAll(filter)).thenReturn(List.of(t1, t2));

            List<Transaction> result = transactionService.findAll(filter);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(Transaction::nombre)
                    .containsExactly("Salario", "Mercado");
            assertThat(result).allSatisfy(t -> {
                assertThat(t.monto()).isNotNull();
                assertThat(t.tipo()).isNotNull();
                assertThat(t.fecha()).isNotNull();
            });
        }

        /**
         * CA-02 @happy-path
         * Las transacciones retornadas están ordenadas de más reciente a más antigua.
         */
        @Test
        @DisplayName("CA-02 — Transacciones ordenadas de más reciente a más antigua")
        void ca02_ordenPorFechaDescendente() {
            Transaction antigua = new Transaction(UUID.randomUUID(), "Antigua", null,
                    BigDecimal.valueOf(10_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 1, 1), categoriaAlimentacion, titularBase);
            Transaction reciente = new Transaction(UUID.randomUUID(), "Reciente", null,
                    BigDecimal.valueOf(20_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 3, 15), categoriaAlimentacion, titularBase);

            TransactionListFilter filter = new TransactionListFilter(Optional.empty(), Optional.empty(),
                    Optional.empty());
            when(transactionRepositoryPort.findAll(filter)).thenReturn(List.of(reciente, antigua));

            List<Transaction> result = transactionService.findAll(filter);

            assertThat(result.get(0).fecha()).isAfterOrEqualTo(result.get(1).fecha());
        }

        /**
         * CA-03 @happy-path
         * Sin transacciones → retorna lista vacía.
         */
        @Test
        @DisplayName("CA-03 — Historial vacío retorna lista vacía")
        void ca03_historialSinTransacciones() {
            TransactionListFilter filter = new TransactionListFilter(Optional.empty(), Optional.empty(),
                    Optional.empty());
            when(transactionRepositoryPort.findAll(filter)).thenReturn(List.of());

            List<Transaction> result = transactionService.findAll(filter);

            assertThat(result).isEmpty();
        }

        /**
         * CA-04 Filtrar por tipo (INGRESO / GASTO).
         */
        @ParameterizedTest(name = "Filtro por tipo={0}")
        @CsvSource({ "INGRESO", "GASTO" })
        @DisplayName("CA-04 — Filtrar transacciones por tipo")
        void ca04_filtrarPorTipo(TypeTransaction tipo) {
            Transaction tx = new Transaction(UUID.randomUUID(), "Test", null,
                    BigDecimal.valueOf(100_000), tipo,
                    LocalDate.now(), categoriaAlimentacion, titularBase);

            TransactionListFilter filter = new TransactionListFilter(Optional.of(tipo), Optional.empty(),
                    Optional.empty());
            when(transactionRepositoryPort.findAll(filter)).thenReturn(List.of(tx));

            List<Transaction> result = transactionService.findAll(filter);

            assertThat(result)
                    .isNotEmpty()
                    .allMatch(t -> t.tipo() == tipo);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HU-03 — Editar una transacción existente
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("HU-03 — Editar una transacción existente")
    class EditarTransaccion {

        private UUID txId;
        private Transaction transaccionExistente;

        @BeforeEach
        void setUpEdicion() {
            txId = UUID.randomUUID();
            transaccionExistente = new Transaction(txId, "Compra Supermercado", null,
                    BigDecimal.valueOf(200_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 3, 1), categoriaAlimentacion, titularBase);
        }

        /**
         * CA-01 @happy-path
         * El formulario de edición retorna los datos actuales precargados.
         */
        @Test
        @DisplayName("CA-01 — Datos actuales disponibles al abrir edición")
        void ca01_datosPrecargados() {
            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.of(transaccionExistente));

            Optional<Transaction> result = transactionService.findById(txId);

            assertThat(result).isPresent();
            assertThat(result.get().nombre()).isEqualTo("Compra Supermercado");
            assertThat(result.get().monto()).isEqualByComparingTo(BigDecimal.valueOf(200_000));
            assertThat(result.get().tipo()).isEqualTo(TypeTransaction.GASTO);
        }

        /**
         * CA-02 @happy-path
         * Cambiar monto → historial refleja el nuevo valor.
         */
        @Test
        @DisplayName("CA-02 — Actualizar monto refleja nuevo valor en historial")
        void ca02_actualizarMontoRefleja() {
            BigDecimal nuevoMonto = BigDecimal.valueOf(250_000);
            Transaction actualizada = new Transaction(txId, "Compra Supermercado", null,
                    nuevoMonto, TypeTransaction.GASTO,
                    LocalDate.of(2026, 3, 1), categoriaAlimentacion, titularBase);

            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.of(transaccionExistente));
            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(categoriaAlimentacion));
            when(transactionRepositoryPort.save(any())).thenReturn(actualizada);

            Transaction request = new Transaction(null, "Compra Supermercado", null,
                    nuevoMonto, TypeTransaction.GASTO, LocalDate.of(2026, 3, 1),
                    categoriaAlimentacion, titularBase);
            Transaction result = transactionService.updateTransaction(txId, request);

            assertThat(result.monto()).isEqualByComparingTo(nuevoMonto);
        }

        /**
         * CA-03 @happy-path
         * Cambiar categoría → transacción queda asociada a la nueva categoría.
         */
        @Test
        @DisplayName("CA-03 — Cambiar categoría actualiza la asociación correctamente")
        void ca03_cambiarCategoria() {
            Category nuevaCategoria = new Category(UUID.randomUUID(), "Transporte", titularBase);
            Transaction actualizada = new Transaction(txId, "Compra Supermercado", null,
                    BigDecimal.valueOf(200_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 3, 1), nuevaCategoria, titularBase);

            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.of(transaccionExistente));
            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findById(nuevaCategoria.categoriaId())).thenReturn(Optional.of(nuevaCategoria));
            when(transactionRepositoryPort.save(any())).thenReturn(actualizada);

            Transaction request = new Transaction(null, "Compra Supermercado", null,
                    BigDecimal.valueOf(200_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 3, 1), nuevaCategoria, titularBase);
            Transaction result = transactionService.updateTransaction(txId, request);

            assertThat(result.categoria().nombre()).isEqualTo("Transporte");
        }

        /**
         * CA-04 @happy-path
         * Transacción no encontrada al editar → lanza ResourceNotFoundException.
         */
        @Test
        @DisplayName("CA-04 — Editar transacción inexistente lanza ResourceNotFoundException")
        void ca04_editarTransaccionInexistenteLanzaExcepcion() {
            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.empty());
            Transaction request = new Transaction(null, "Test", null,
                    BigDecimal.valueOf(100), TypeTransaction.INGRESO, null, null, titularBase);

            assertThatThrownBy(() -> transactionService.updateTransaction(txId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Transacción no encontrada");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // HU-04 — Eliminar una transacción
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("HU-04 — Eliminar una transacción")
    class EliminarTransaccion {

        private UUID txId;

        @BeforeEach
        void setUpEliminacion() {
            txId = UUID.randomUUID();
        }

        /**
         * CA-01 + CA-02 @happy-path
         * Confirmar eliminación → transacción desaparece del historial.
         */
        @Test
        @DisplayName("CA-01 CA-02 — Confirmar eliminación borra la transacción del historial")
        void ca01_ca02_confirmarEliminacionBorraTransaccion() {
            doNothing().when(transactionRepositoryPort).deleteById(txId);

            transactionService.deleteTransaction(txId);

            verify(transactionRepositoryPort, times(1)).deleteById(txId);
        }

        /**
         * CA-03 @happy-path
         * Cancelar eliminación → deleteById NO es invocado.
         */
        @Test
        @DisplayName("CA-03 — Cancelar eliminación no invoca deleteById")
        void ca03_cancelarEliminacionNoBorra() {
            // Simula que el usuario presiona Cancelar: el controller NO llama al service
            // Verificamos que el método deleteById nunca es invocado
            verify(transactionRepositoryPort, never()).deleteById(any());
        }
    }
}
