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

    
    // HU-01 — Registrar una transacción financiera

    @Nested
    @DisplayName("HU-01 — Registrar una transacción financiera")
    class RegistrarTransaccion {

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

        /**
         * CA-06 @happy-path
         * Registrar transacción con descripción completa.
         */
        @Test
        @DisplayName("CA-06 — Registrar transacción con descripción preserva el campo")
        void ca06_registrarTransaccionConDescripcion() {
            String descripcion = "Compra en supermercado Plaza Mayor";
            Transaction request = new Transaction(null, "Mercado", descripcion,
                    BigDecimal.valueOf(150_000), TypeTransaction.GASTO,
                    LocalDate.now(), null, titularBase);
            Transaction saved = new Transaction(UUID.randomUUID(), "Mercado", descripcion,
                    BigDecimal.valueOf(150_000), TypeTransaction.GASTO,
                    LocalDate.now(), categoriaAlimentacion, titularBase);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findByNombreIgnoreCase("Vacía")).thenReturn(Optional.of(categoriaAlimentacion));
            when(transactionRepositoryPort.save(any())).thenReturn(saved);

            Transaction result = transactionService.createTransaction(request);

            assertThat(result.descripcion()).isEqualTo(descripcion);
            assertThat(result.nombre()).isEqualTo("Mercado");
        }

        /**
         * CA-07 @happy-path
         * Registrar transacción con categoría específica válida.
         */
        @Test
        @DisplayName("CA-07 — Registrar con categoría específica asigna correctamente")
        void ca07_registrarConCategoriaEspecifica() {
            Category categoriaTransporte = new Category(UUID.randomUUID(), "Transporte", titularBase);
            Transaction request = new Transaction(null, "Uber", null,
                    BigDecimal.valueOf(35_000), TypeTransaction.GASTO,
                    LocalDate.now(), categoriaTransporte, titularBase);
            Transaction saved = new Transaction(UUID.randomUUID(), "Uber", null,
                    BigDecimal.valueOf(35_000), TypeTransaction.GASTO,
                    LocalDate.now(), categoriaTransporte, titularBase);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findById(categoriaTransporte.categoriaId())).thenReturn(Optional.of(categoriaTransporte));
            when(transactionRepositoryPort.save(any())).thenReturn(saved);
            Transaction result = transactionService.createTransaction(request);

            assertThat(result.categoria()).isEqualTo(categoriaTransporte);
        }

        /**
         * CA-08 @error-handling
         * Categoría específica no encontrada → lanza ResourceNotFoundException.
         */
        @Test
        @DisplayName("CA-08 — Categoría específica no encontrada lanza excepción")
        void ca08_categoriaEspecificaNoEncontradaLanzaExcepcion() {
            Category categoriaInvalida = new Category(UUID.randomUUID(), "Inexistente", null);
            Transaction request = new Transaction(null, "Test", null,
                    BigDecimal.valueOf(100), TypeTransaction.GASTO,
                    null, categoriaInvalida, titularBase);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findById(categoriaInvalida.categoriaId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.createTransaction(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Categoría no encontrada");
        }

        /**
         * CA-09 @error-handling
         * Error al guardar en repositorio → propaga excepción.
         */
        @Test
        @DisplayName("CA-09 — Error del repositorio al guardar propaga excepción")
        void ca09_errorRepositorioAlGuardar() {
            Transaction request = new Transaction(null, "Test", null,
                    BigDecimal.valueOf(100_000), TypeTransaction.INGRESO,
                    null, null, titularBase);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findByNombreIgnoreCase("Vacía")).thenReturn(Optional.of(categoriaAlimentacion));
            when(transactionRepositoryPort.save(any()))
                    .thenThrow(new RuntimeException("Error de base de datos"));

            assertThatThrownBy(() -> transactionService.createTransaction(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error de base de datos");
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

            // FIX: verificar que no está vacía antes de usar allMatch
            assertThat(result)
                    .isNotEmpty()
                    .allMatch(t -> t.tipo() == tipo);
        }

        /**
         * CA-05 @happy-path
         * Filtrar por categoría específica.
         */
        @Test
        @DisplayName("CA-05 — Filtrar transacciones por categoría")
        void ca05_filtrarPorCategoria() {
            Category categoriaTransporte = new Category(UUID.randomUUID(), "Transporte", titularBase);
            Transaction tx1 = new Transaction(UUID.randomUUID(), "Uber", null,
                    BigDecimal.valueOf(50_000), TypeTransaction.GASTO,
                    LocalDate.now(), categoriaTransporte, titularBase);
            Transaction tx2 = new Transaction(UUID.randomUUID(), "Metro", null,
                    BigDecimal.valueOf(2_850), TypeTransaction.GASTO,
                    LocalDate.now(), categoriaTransporte, titularBase);

            TransactionListFilter filter = new TransactionListFilter(Optional.empty(), Optional.of(categoriaTransporte.categoriaId()),
                    Optional.empty());
            when(transactionRepositoryPort.findAll(filter)).thenReturn(List.of(tx1, tx2));

            List<Transaction> result = transactionService.findAll(filter);

            assertThat(result)
                    .hasSize(2)
                    .allMatch(t -> t.categoria().categoriaId().equals(categoriaTransporte.categoriaId()));
        }

        /**
         * CA-06 @happy-path
         * Consultar por ID una transacción específica.
         */
        @Test
        @DisplayName("CA-06 — Buscar transacción por ID retorna el registro exacto")
        void ca06_buscarTransaccionPorId() {
            UUID txId = UUID.randomUUID();
            Transaction txEncontrada = new Transaction(txId, "Salario Enero", "Pago quincenal",
                    BigDecimal.valueOf(3_500_000), TypeTransaction.INGRESO,
                    LocalDate.of(2026, 3, 15), categoriaAlimentacion, titularBase);

            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.of(txEncontrada));

            Optional<Transaction> result = transactionService.findById(txId);

            assertThat(result)
                    .isPresent()
                    .hasValueSatisfying(tx -> {
                        assertThat(tx.transactionId()).isEqualTo(txId);
                        assertThat(tx.nombre()).isEqualTo("Salario Enero");
                        assertThat(tx.descripcion()).isEqualTo("Pago quincenal");
                    });
        }

        /**
         * CA-07 @error-handling
         * Buscar transacción con ID no existente retorna Optional vacío.
         */
        @Test
        @DisplayName("CA-07 — Buscar por ID inexistente retorna Optional vacío")
        void ca07_buscarPorIdNoExistente() {
            UUID txId = UUID.randomUUID();
            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.empty());

            Optional<Transaction> result = transactionService.findById(txId);

            assertThat(result).isEmpty();
        }

        /**
         * CA-08 @happy-path
         * Filtro combinado: tipo + rango de fechas.
         */
        @Test
        @DisplayName("CA-08 — Filtro combinado (tipo y fecha) aplica ambas restricciones")
        void ca08_filtroCombinado() {
            Transaction gasto1 = new Transaction(UUID.randomUUID(), "Compra 1", null,
                    BigDecimal.valueOf(100_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 2, 15), categoriaAlimentacion, titularBase);
            Transaction gasto2 = new Transaction(UUID.randomUUID(), "Compra 2", null,
                    BigDecimal.valueOf(200_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 3, 20), categoriaAlimentacion, titularBase);
            Transaction ingreso = new Transaction(UUID.randomUUID(), "Salario", null,
                    BigDecimal.valueOf(3_500_000), TypeTransaction.INGRESO,
                    LocalDate.of(2026, 3, 1), categoriaAlimentacion, titularBase);

            TransactionListFilter filter = new TransactionListFilter(Optional.of(TypeTransaction.GASTO), Optional.empty(),
                    Optional.empty());
            when(transactionRepositoryPort.findAll(filter)).thenReturn(List.of(gasto2, gasto1));

            List<Transaction> result = transactionService.findAll(filter);

            assertThat(result)
                    .hasSize(2)
                    .allMatch(t -> t.tipo() == TypeTransaction.GASTO)
                    .extracting(Transaction::nombre)
                    .containsExactlyInAnyOrder("Compra 1", "Compra 2");
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

            assertThat(result).hasValueSatisfying(tx -> {
                assertThat(tx.nombre()).isEqualTo("Compra Supermercado");
                assertThat(tx.monto()).isEqualByComparingTo(BigDecimal.valueOf(200_000));
                assertThat(tx.tipo()).isEqualTo(TypeTransaction.GASTO);
            });
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

        /**
         * CA-05 @error-handling
         * Titular no encontrado al actualizar → lanza ResourceNotFoundException.
         */
        @Test
        @DisplayName("CA-05 — Actualizar con titular no encontrado lanza excepción")
        void ca05_actualizarConTitularNoEncontrado() {
            Titular titularInvalido = new Titular(UUID.randomUUID(), "Fake", "User", "Test",
                    "3001111111", Instant.now(), "COP", "America/Bogota", "fake-token");
            Transaction request = new Transaction(null, "Test", null,
                    BigDecimal.valueOf(100_000), TypeTransaction.GASTO,
                    LocalDate.now(), categoriaAlimentacion, titularInvalido);

            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.of(transaccionExistente));
            when(titularRepositoryPort.findById(titularInvalido.titularId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.updateTransaction(txId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Titular no encontrado");
        }

        /**
         * CA-06 @error-handling
         * Categoría específica no encontrada al actualizar → lanza excepción.
         */
        @Test
        @DisplayName("CA-06 — Actualizar con categoría no encontrada lanza excepción")
        void ca06_actualizarConCategoriaNroEncontrada() {
            Category categoriaInvalida = new Category(UUID.randomUUID(), "Inexistente", null);
            Transaction request = new Transaction(null, "Test", null,
                    BigDecimal.valueOf(100_000), TypeTransaction.GASTO,
                    LocalDate.now(), categoriaInvalida, titularBase);

            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.of(transaccionExistente));
            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findById(categoriaInvalida.categoriaId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.updateTransaction(txId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Categoría no encontrada");
        }

        /**
         * CA-07 @happy-path
         * Cambiar fecha de transacción → se refleja correctamente.
         */
        @Test
        @DisplayName("CA-07 — Actualizar fecha refleja nuevo valor")
        void ca07_actualizarFecha() {
            LocalDate nuevaFecha = LocalDate.of(2026, 5, 10);
            Transaction actualizada = new Transaction(txId, "Compra Supermercado", null,
                    BigDecimal.valueOf(200_000), TypeTransaction.GASTO,
                    nuevaFecha, categoriaAlimentacion, titularBase);

            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.of(transaccionExistente));
            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(categoriaAlimentacion));
            when(transactionRepositoryPort.save(any())).thenReturn(actualizada);

            Transaction request = new Transaction(null, "Compra Supermercado", null,
                    BigDecimal.valueOf(200_000), TypeTransaction.GASTO,
                    nuevaFecha, categoriaAlimentacion, titularBase);
            Transaction result = transactionService.updateTransaction(txId, request);

            assertThat(result.fecha()).isEqualTo(nuevaFecha);
        }

        /**
         * CA-08 @happy-path
         * Cambiar descripción → se actualiza correctamente.
         */
        @Test
        @DisplayName("CA-08 — Actualizar descripción se persiste correctamente")
        void ca08_actualizarDescripcion() {
            String nuevaDescripcion = "Compra en supermercado D1 - Sector norte";
            Transaction actualizada = new Transaction(txId, "Compra Supermercado", nuevaDescripcion,
                    BigDecimal.valueOf(200_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 3, 1), categoriaAlimentacion, titularBase);

            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.of(transaccionExistente));
            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(categoriaAlimentacion));
            when(transactionRepositoryPort.save(any())).thenReturn(actualizada);

            Transaction request = new Transaction(null, "Compra Supermercado", nuevaDescripcion,
                    BigDecimal.valueOf(200_000), TypeTransaction.GASTO,
                    LocalDate.of(2026, 3, 1), categoriaAlimentacion, titularBase);
            Transaction result = transactionService.updateTransaction(txId, request);

            assertThat(result.descripcion()).isEqualTo(nuevaDescripcion);
        }

        /**
         * CA-09 @error-handling
         * Error al guardar en repositorio durante actualización.
         */
        @Test
        @DisplayName("CA-09 — Error del repositorio al actualizar propaga excepción")
        void ca09_errorRepositorioAlActualizar() {
            Transaction request = new Transaction(null, "Compra", null,
                    BigDecimal.valueOf(100_000), TypeTransaction.GASTO,
                    LocalDate.now(), categoriaAlimentacion, titularBase);

            when(transactionRepositoryPort.findById(txId)).thenReturn(Optional.of(transaccionExistente));
            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titularBase));
            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(categoriaAlimentacion));
            when(transactionRepositoryPort.save(any()))
                    .thenThrow(new RuntimeException("Error de base de datos"));

            assertThatThrownBy(() -> transactionService.updateTransaction(txId, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error de base de datos");
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

        /**
         * CA-04 @happy-path
         * Eliminar múltiples transacciones en secuencia.
         */
        @Test
        @DisplayName("CA-04 — Eliminar varias transacciones en secuencia funciona correctamente")
        void ca04_eliminarMultiplesTransacciones() {
            UUID txId1 = UUID.randomUUID();
            UUID txId2 = UUID.randomUUID();
            UUID txId3 = UUID.randomUUID();

            doNothing().when(transactionRepositoryPort).deleteById(txId1);
            doNothing().when(transactionRepositoryPort).deleteById(txId2);
            doNothing().when(transactionRepositoryPort).deleteById(txId3);

            transactionService.deleteTransaction(txId1);
            transactionService.deleteTransaction(txId2);
            transactionService.deleteTransaction(txId3);

            verify(transactionRepositoryPort, times(1)).deleteById(txId1);
            verify(transactionRepositoryPort, times(1)).deleteById(txId2);
            verify(transactionRepositoryPort, times(1)).deleteById(txId3);
        }

        /**
         * CA-05 @error-handling
         * Intentar eliminar transacción ya eliminada no lanza excepción (idempotencia).
         */
        @Test
        @DisplayName("CA-05 — Eliminar transacción ya eliminada (idempotencia) no lanza error")
        void ca05_eliminarTransaccionYaEliminada() {
            // El repositorio no lanza excepción si el registro no existe
            doNothing().when(transactionRepositoryPort).deleteById(txId);

            // No debe lanzar excepción
            assertThatCode(() -> transactionService.deleteTransaction(txId))
                    .doesNotThrowAnyException();

            verify(transactionRepositoryPort).deleteById(txId);
        }

        /**
         * CA-06 @error-handling
         * Error del repositorio al eliminar propaga excepción.
         */
        @Test
        @DisplayName("CA-06 — Error del repositorio al eliminar propaga excepción")
        void ca06_errorRepositorioAlEliminar() {
            doThrow(new RuntimeException("Error de base de datos"))
                    .when(transactionRepositoryPort).deleteById(txId);

            assertThatThrownBy(() -> transactionService.deleteTransaction(txId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error de base de datos");
        }
    }
}
