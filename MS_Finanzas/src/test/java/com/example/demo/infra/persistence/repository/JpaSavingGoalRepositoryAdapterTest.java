package com.example.demo.infra.persistence.repository;

import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.exception.SavingGoalNotFoundException;
import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.SavingGoalEntityMapper;
import com.example.demo.infra.persistence.entity.SavingGoalEntity;
import com.example.demo.infra.persistence.entity.TitularEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JpaSavingGoalRepositoryAdapter")
class JpaSavingGoalRepositoryAdapterTest {

    @Mock private JpaSavingGoalRepository jpaSavingGoalRepository;
    @Mock private JpaTitularRepository jpaTitularRepository;
    @Mock private SavingGoalEntityMapper mapper;

    @InjectMocks private JpaSavingGoalRepositoryAdapter adapter;

    private UUID goalId;
    private UUID titularId;
    private Titular titular;
    private SavingGoal savingGoal;
    private SavingGoalEntity entity;
    private TitularEntity titularEntity;

    @BeforeEach
    void setUp() {
        goalId = UUID.randomUUID();
        titularId = UUID.randomUUID();

        titular = new Titular(titularId, "Laura", "Rios", "Munoz",
                "3109876543", Instant.now(), "COP", "America/Bogota", "tkn");

        savingGoal = new SavingGoal(goalId, "Vacaciones", 5000000.0, 30,
                GoalStatus.EN_PROGRESO, LocalDate.of(2025, 12, 31), titular);

        titularEntity = new TitularEntity();
        titularEntity.setTitularId(titularId);

        entity = new SavingGoalEntity();
        entity.setGoalId(goalId);
        entity.setNombre("Vacaciones");
        entity.setMontoObjetivo(5000000.0);
        entity.setAvance(30);
        entity.setEstado(GoalStatus.EN_PROGRESO);
        entity.setFechaLimite(LocalDate.of(2025, 12, 31));
        entity.setTitular(titularEntity);
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("guarda meta cuando el titular existe")
        void save_titularExists_savesAndReturnsDomain() {
            when(mapper.toEntity(savingGoal)).thenReturn(entity);
            when(jpaTitularRepository.findById(titularId)).thenReturn(Optional.of(titularEntity));
            when(jpaSavingGoalRepository.save(entity)).thenReturn(entity);
            when(mapper.toDomain(entity)).thenReturn(savingGoal);

            SavingGoal result = adapter.save(savingGoal);

            assertThat(result).isEqualTo(savingGoal);
            assertThat(entity.getTitular()).isEqualTo(titularEntity);
        }

        @Test
        @DisplayName("lanza ResourceNotFoundException si titular no existe")
        void save_titularMissing_throwsResourceNotFoundException() {
            when(mapper.toEntity(savingGoal)).thenReturn(entity);
            when(jpaTitularRepository.findById(titularId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adapter.save(savingGoal))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(titularId.toString());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("retorna dominio cuando existe")
        void findById_existing_returnsDomain() {
            when(jpaSavingGoalRepository.findById(goalId)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(savingGoal);

            assertThat(adapter.findById(goalId)).isPresent().contains(savingGoal);
        }

        @Test
        @DisplayName("retorna vacío cuando no existe")
        void findById_missing_returnsEmpty() {
            when(jpaSavingGoalRepository.findById(goalId)).thenReturn(Optional.empty());

            assertThat(adapter.findById(goalId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("retorna lista mapeada")
        void findAll_returnsMappedList() {
            when(jpaSavingGoalRepository.findAll()).thenReturn(List.of(entity));
            when(mapper.toDomain(entity)).thenReturn(savingGoal);

            assertThat(adapter.findAll()).hasSize(1).contains(savingGoal);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("actualiza campos y retorna dominio")
        void update_existing_updatesAndReturnsDomain() {
            SavingGoal updated = new SavingGoal(goalId, "Fondo emergencia", 8000000.0, 50,
                    GoalStatus.EN_PROGRESO, LocalDate.of(2026, 6, 30), titular);

            when(jpaSavingGoalRepository.findById(goalId)).thenReturn(Optional.of(entity));
            when(jpaSavingGoalRepository.save(entity)).thenReturn(entity);
            when(mapper.toDomain(entity)).thenReturn(updated);

            SavingGoal result = adapter.update(goalId, updated);

            assertThat(result).isEqualTo(updated);
            assertThat(entity.getNombre()).isEqualTo("Fondo emergencia");
            assertThat(entity.getMontoObjetivo()).isEqualTo(8000000.0);
        }

        @Test
        @DisplayName("lanza SavingGoalNotFoundException si no existe")
        void update_missing_throwsSavingGoalNotFoundException() {
            when(jpaSavingGoalRepository.findById(goalId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adapter.update(goalId, savingGoal))
                    .isInstanceOf(SavingGoalNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {

        @Test
        @DisplayName("delega al repositorio JPA")
        void deleteById_delegatesToJpa() {
            adapter.deleteById(goalId);
            verify(jpaSavingGoalRepository).deleteById(goalId);
        }
    }

    @Nested
    @DisplayName("existsByNombre")
    class ExistsByNombre {

        @Test
        @DisplayName("retorna true cuando existe")
        void existsByNombre_existing_returnsTrue() {
            when(jpaSavingGoalRepository.existsByNombre("Vacaciones")).thenReturn(true);
            assertThat(adapter.existsByNombre("Vacaciones")).isTrue();
        }

        @Test
        @DisplayName("retorna false cuando no existe")
        void existsByNombre_missing_returnsFalse() {
            when(jpaSavingGoalRepository.existsByNombre("X")).thenReturn(false);
            assertThat(adapter.existsByNombre("X")).isFalse();
        }
    }

    @Nested
    @DisplayName("updateAvance")
    class UpdateAvance {

        @Test
        @DisplayName("actualiza avance y estado correctamente")
        void updateAvance_existing_updatesAndReturns() {
            SavingGoal completado = new SavingGoal(goalId, "Vacaciones", 5000000.0, 100,
                    GoalStatus.COMPLETADA, LocalDate.of(2025, 12, 31), titular);

            when(jpaSavingGoalRepository.findById(goalId)).thenReturn(Optional.of(entity));
            when(jpaSavingGoalRepository.save(entity)).thenReturn(entity);
            when(mapper.toDomain(entity)).thenReturn(completado);

            SavingGoal result = adapter.updateAvance(goalId, 100, GoalStatus.COMPLETADA);

            assertThat(result.avance()).isEqualTo(100);
            assertThat(result.estado()).isEqualTo(GoalStatus.COMPLETADA);
            assertThat(entity.getAvance()).isEqualTo(100);
            assertThat(entity.getEstado()).isEqualTo(GoalStatus.COMPLETADA);
        }

        @Test
        @DisplayName("lanza SavingGoalNotFoundException si no existe")
        void updateAvance_missing_throwsSavingGoalNotFoundException() {
            when(jpaSavingGoalRepository.findById(goalId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adapter.updateAvance(goalId, 50, GoalStatus.EN_PROGRESO))
                    .isInstanceOf(SavingGoalNotFoundException.class);
        }
    }
}
