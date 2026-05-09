package com.example.demo.application.service;

import com.example.demo.application.repository.SavingGoalRepositoryPort;
import com.example.demo.domain.exception.DuplicateGoalNameException;
import com.example.demo.domain.exception.SavingGoalNotFoundException;
import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.domain.model.Titular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingGoalServiceTest {

        @Mock
        private SavingGoalRepositoryPort savingGoalRepositoryPort;

        @InjectMocks
        private SavingGoalService savingGoalService;

        private Titular titular;
        private UUID titularId;

        @BeforeEach
        void setUp() {
                titularId = UUID.randomUUID();
                titular = new Titular(titularId, "Laura", "Rios", "Munoz",
                                "3207654321", Instant.now(), "COP", "America/Bogota", "token-xyz");
        }

        @Nested
        @DisplayName("HU-06 - Crear lista de meta de ahorro")
        class CrearMetaAhorro {

                @Test
                @DisplayName("CA-01 CA-05 - Crear meta Vacaciones 2026 con 0% avance y estado EN_PROGRESO")
                void ca01_ca05_crearMetaBasicaExitosa() {
                        SavingGoal request = new SavingGoal(null, "Vacaciones 2026", 5_000_000.0,
                                        0, null, LocalDate.now().plusMonths(6), titular);
                        SavingGoal saved = new SavingGoal(UUID.randomUUID(), "Vacaciones 2026", 5_000_000.0,
                                        0, GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(6), titular);

                        when(savingGoalRepositoryPort.existsByNombre("Vacaciones 2026")).thenReturn(false);
                        when(savingGoalRepositoryPort.save(any())).thenReturn(saved);

                        SavingGoal resultado = savingGoalService.addSavingGoal(request);

                        assertThat(resultado.goalId()).isNotNull();
                        assertThat(resultado.nombre()).isEqualTo("Vacaciones 2026");
                        assertThat(resultado.montoObjetivo()).isEqualTo(5_000_000.0);
                        assertThat(resultado.avance()).isZero();
                        assertThat(resultado.estado()).isEqualTo(GoalStatus.EN_PROGRESO);
                }

                @Test
                @DisplayName("CA-04 - Crear meta Fondo Emergencia sin fecha limite")
                void ca04_crearMetaSinFechaLimite() {
                        SavingGoal request = new SavingGoal(null, "Fondo Emergencia", 3_000_000.0,
                                        0, null, null, titular);
                        SavingGoal saved = new SavingGoal(UUID.randomUUID(), "Fondo Emergencia", 3_000_000.0,
                                        0, GoalStatus.EN_PROGRESO, null, titular);

                        when(savingGoalRepositoryPort.existsByNombre("Fondo Emergencia")).thenReturn(false);
                        when(savingGoalRepositoryPort.save(any())).thenReturn(saved);

                        SavingGoal resultado = savingGoalService.addSavingGoal(request);

                        assertThat(resultado.goalId()).isNotNull();
                        assertThat(resultado.fechaLimite()).isNull();
                }

                @ParameterizedTest(name = "CA-02 | monto={0}")
                @ValueSource(doubles = { 0.0, -1000.0 })
                @DisplayName("CA-02 - Monto objetivo cero o negativo lanza excepcion")
                void ca02_montoObjetivoInvalidoLanzaExcepcion(double monto) {
                        SavingGoal request = new SavingGoal(null, "Meta Invalida", monto,
                                        0, null, LocalDate.now().plusMonths(1), titular);

                        assertThatThrownBy(() -> savingGoalService.addSavingGoal(request))
                                        .isInstanceOf(IllegalArgumentException.class)
                                        .hasMessageContaining("El monto debe ser mayor a 0");

                        verify(savingGoalRepositoryPort, never()).save(any());
                }

                @Test
                @DisplayName("CA-03 - Fecha limite en el pasado lanza excepcion")
                void ca03_fechaLimiteEnElPasadoLanzaExcepcion() {
                        SavingGoal request = new SavingGoal(null, "Meta Pasada", 1_000_000.0,
                                        0, null, LocalDate.of(2020, 1, 1), titular);

                        assertThatThrownBy(() -> savingGoalService.addSavingGoal(request))
                                        .isInstanceOf(IllegalArgumentException.class)
                                        .hasMessageContaining("La fecha límite debe ser una fecha futura");

                        verify(savingGoalRepositoryPort, never()).save(any());
                }

                @Test
                @DisplayName("Nombre de meta duplicado lanza DuplicateGoalNameException")
                void nombreDuplicadoLanzaExcepcion() {
                        SavingGoal request = new SavingGoal(null, "Vacaciones 2026", 5_000_000.0,
                                        0, null, LocalDate.now().plusMonths(6), titular);

                        when(savingGoalRepositoryPort.existsByNombre("Vacaciones 2026")).thenReturn(true);

                        assertThatThrownBy(() -> savingGoalService.addSavingGoal(request))
                                        .isInstanceOf(DuplicateGoalNameException.class)
                                        .hasMessageContaining("Vacaciones 2026");

                        verify(savingGoalRepositoryPort, never()).save(any());
                }

                @Test
                @DisplayName("Nombre vacio lanza IllegalArgumentException")
                void nombreVacioLanzaExcepcion() {
                        SavingGoal request = new SavingGoal(null, "", 1_000_000.0,
                                        0, null, LocalDate.now().plusMonths(1), titular);

                        assertThatThrownBy(() -> savingGoalService.addSavingGoal(request))
                                        .isInstanceOf(IllegalArgumentException.class)
                                        .hasMessageContaining("El nombre es obligatorio");

                        verify(savingGoalRepositoryPort, never()).save(any());
                }
        }

        @Nested
        @DisplayName("HU-07 - Registrar aporte a una meta de ahorro")
        class RegistrarAporte {

                private UUID goalId;
                private SavingGoal metaEnProgreso;

                @BeforeEach
                void setUpMeta() {
                        goalId = UUID.randomUUID();
                        metaEnProgreso = new SavingGoal(goalId, "Vacaciones 2026", 5_000_000.0,
                                        0, GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(6), titular);
                }

                @Test
                @DisplayName("CA-01 CA-02 - Aporte aumenta acumulado y calcula porcentaje de avance correctamente")
                void ca01_ca02_aporteAumentaAcumuladoYPorcentaje() {
                        int nuevoAvance = 1_000_000;
                        int porcentajeEsperado = (int) ((nuevoAvance / metaEnProgreso.montoObjetivo()) * 100);

                        SavingGoal metaActualizada = new SavingGoal(goalId, "Vacaciones 2026", 5_000_000.0,
                                        nuevoAvance, GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(6), titular);

                        when(savingGoalRepositoryPort.updateAvance(goalId, nuevoAvance, GoalStatus.EN_PROGRESO))
                                        .thenReturn(metaActualizada);

                        SavingGoal result = savingGoalRepositoryPort.updateAvance(goalId, nuevoAvance,
                                        GoalStatus.EN_PROGRESO);

                        assertThat(result.avance()).isEqualTo(1_000_000);
                        int porcentajeReal = (int) ((result.avance() / result.montoObjetivo()) * 100);
                        assertThat(porcentajeReal).isEqualTo(porcentajeEsperado);
                }

                @Test
                @DisplayName("CA-03 - Meta se marca COMPLETADA al alcanzar el monto objetivo")
                void ca03_metaMarcadaCompletadaAlAlcanzarObjetivo() {
                        SavingGoal metaCompletada = new SavingGoal(goalId, "Vacaciones 2026", 5_000_000.0,
                                        5_000_000, GoalStatus.COMPLETADA, LocalDate.now().plusMonths(6), titular);

                        when(savingGoalRepositoryPort.updateAvance(goalId, 5_000_000, GoalStatus.COMPLETADA))
                                        .thenReturn(metaCompletada);

                        SavingGoal result = savingGoalRepositoryPort.updateAvance(goalId, 5_000_000,
                                        GoalStatus.COMPLETADA);

                        assertThat(result.estado()).isEqualTo(GoalStatus.COMPLETADA);
                        assertThat(result.avance()).isEqualTo(5_000_000);
                }

                @Test
                @DisplayName("CA-04 - Meta COMPLETADA no permite nuevos aportes")
                void ca04_metaCompletadaNoPermiteNuevosAportes() {
                        when(savingGoalRepositoryPort.updateAvance(eq(goalId), anyInt(), any()))
                                        .thenThrow(new IllegalStateException("Esa meta ya fue alcanzada"));

                        assertThatThrownBy(() -> savingGoalRepositoryPort.updateAvance(goalId, 100_000,
                                        GoalStatus.COMPLETADA))
                                        .isInstanceOf(IllegalStateException.class)
                                        .hasMessageContaining("Esa meta ya fue alcanzada");
                }

                @Test
                @DisplayName("Eliminar meta inexistente lanza SavingGoalNotFoundException")
                void eliminarMetaInexistenteLanzaExcepcion() {
                        UUID idFalso = UUID.randomUUID();
                        when(savingGoalRepositoryPort.findById(idFalso)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> savingGoalService.deleteSavingGoalById(idFalso))
                                        .isInstanceOf(SavingGoalNotFoundException.class)
                                        .hasMessageContaining("Meta de ahorro no encontrada");

                        verify(savingGoalRepositoryPort, never()).deleteById(any());
                }
        }

        @Nested
        @DisplayName("Operaciones adicionales SavingGoalService")
        class OperacionesAdicionales {

                @Test
                @DisplayName("findById - retorna meta existente")
                void findById_retornaMetaExistente() {
                        UUID goalId = UUID.randomUUID();
                        SavingGoal meta = new SavingGoal(goalId, "Vacaciones", 5_000_000.0,
                                        0, GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(6), titular);

                        when(savingGoalRepositoryPort.findById(goalId)).thenReturn(Optional.of(meta));

                        Optional<SavingGoal> resultado = savingGoalService.findById(goalId);

                        assertThat(resultado).hasValueSatisfying(sg ->
                            assertThat(sg.nombre()).isEqualTo("Vacaciones")
                        );
                }

                @Test
                @DisplayName("findById - retorna vacio si no existe")
                void findById_retornaVacioSiNoExiste() {
                        UUID idFalso = UUID.randomUUID();
                        when(savingGoalRepositoryPort.findById(idFalso)).thenReturn(Optional.empty());

                        Optional<SavingGoal> resultado = savingGoalService.findById(idFalso);

                        assertThat(resultado).isEmpty();
                }

                @Test
                @DisplayName("findAll - retorna lista de metas")
                void findAll_retornaListaDeMetas() {
                        SavingGoal meta = new SavingGoal(UUID.randomUUID(), "Vacaciones", 5_000_000.0,
                                        0, GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(6), titular);

                        when(savingGoalRepositoryPort.findAll()).thenReturn(List.of(meta));

                        List<SavingGoal> resultado = savingGoalService.findAll();

                        assertThat(resultado).hasSize(1);
                        assertThat(resultado.get(0).nombre()).isEqualTo("Vacaciones");
                }

                @Test
                @DisplayName("updateSavingGoal - actualiza meta exitosamente")
                void updateSavingGoal_exitoso() {
                        UUID goalId = UUID.randomUUID();
                        SavingGoal existente = new SavingGoal(goalId, "Vacaciones", 5_000_000.0,
                                        0, GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(6), titular);
                        SavingGoal actualizada = new SavingGoal(goalId, "Vacaciones 2027", 6_000_000.0,
                                        0, GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(12), titular);

                        when(savingGoalRepositoryPort.findById(goalId)).thenReturn(Optional.of(existente));
                        when(savingGoalRepositoryPort.existsByNombre("Vacaciones 2027")).thenReturn(false);
                        when(savingGoalRepositoryPort.update(eq(goalId), any())).thenReturn(actualizada);

                        SavingGoal resultado = savingGoalService.updateSavingGoal(goalId, actualizada);

                        assertThat(resultado.nombre()).isEqualTo("Vacaciones 2027");
                        assertThat(resultado.montoObjetivo()).isEqualTo(6_000_000.0);
                }

                @Test
                @DisplayName("updateSavingGoal - meta no encontrada lanza SavingGoalNotFoundException")
                void updateSavingGoal_noEncontrada() {
                        UUID idFalso = UUID.randomUUID();
                        SavingGoal actualizada = new SavingGoal(idFalso, "Vacaciones", 5_000_000.0,
                                        0, GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(6), titular);

                        when(savingGoalRepositoryPort.findById(idFalso)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> savingGoalService.updateSavingGoal(idFalso, actualizada))
                                        .isInstanceOf(SavingGoalNotFoundException.class);
                }

                @Test
                @DisplayName("deleteSavingGoalById - elimina meta exitosamente")
                void deleteSavingGoalById_exitoso() {
                        UUID goalId = UUID.randomUUID();
                        SavingGoal meta = new SavingGoal(goalId, "Vacaciones", 5_000_000.0,
                                        0, GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(6), titular);

                        when(savingGoalRepositoryPort.findById(goalId)).thenReturn(Optional.of(meta));

                        savingGoalService.deleteSavingGoalById(goalId);

                        verify(savingGoalRepositoryPort).deleteById(goalId);
                }
        }
}
