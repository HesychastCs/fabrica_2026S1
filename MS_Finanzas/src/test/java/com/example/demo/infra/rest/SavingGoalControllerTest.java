package com.example.demo.infra.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.application.service.SavingGoalService;
import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.SavingGoalRequestMapper;
import com.example.demo.infra.mapper.SavingGoalResponseMapper;
import com.example.demo.infra.rest.dto.SavingGoalRequest;
import com.example.demo.infra.rest.dto.SavingGoalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class SavingGoalControllerTest {

    private final StubSavingGoalService savingGoalService = new StubSavingGoalService();

    private SavingGoalController controller;
    private SavingGoalResponseMapper savingGoalResponseMapper;
    private SavingGoalRequestMapper savingGoalRequestMapper;

    private UUID titularId;
    private UUID goalId;
    private SavingGoal goal;
    private SavingGoalResponse response;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        goalId = UUID.randomUUID();
        Titular titular = new Titular(titularId, "Ana", "Lopez", "Garcia", "3001234567", Instant.now(), "COP", "America/Bogota", "token-1");
        goal = new SavingGoal(goalId, "Vacaciones", 5000000.0, 0, GoalStatus.EN_PROGRESO, LocalDate.of(2026, 12, 31), titular);
        response = new SavingGoalResponse(goal.goalId(), goal.nombre(), goal.montoObjetivo(), goal.avance().doubleValue(), goal.estado().name(), goal.fechaLimite(), titularId, titular.nombre());
        savingGoalResponseMapper = value -> response;
        savingGoalRequestMapper = requestValue -> goal;
        savingGoalService.findByIdResult = Optional.of(goal);
        savingGoalService.findAllResult = List.of(goal);
        savingGoalService.addResult = goal;
        savingGoalService.updateResult = goal;
        controller = new SavingGoalController(savingGoalService, savingGoalResponseMapper, savingGoalRequestMapper);
    }

    @Test
    void getSavingGoalById_shouldReturnOkWhenFound() {
        ResponseEntity<SavingGoalResponse> result = controller.getSavingGoalById(goalId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getSavingGoalById_shouldReturnNotFoundWhenMissing() {
        savingGoalService.findByIdResult = Optional.empty();

        ResponseEntity<SavingGoalResponse> result = controller.getSavingGoalById(UUID.randomUUID());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllSavingGoals_shouldReturnMappedGoals() {
        ResponseEntity<List<SavingGoalResponse>> result = controller.getAllSavingGoals();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void createSavingGoal_shouldReturnCreated() {
        SavingGoalRequest request = new SavingGoalRequest("Vacaciones", 5000000.0, LocalDate.of(2026, 12, 31), titularId);

        ResponseEntity<SavingGoalResponse> result = controller.createSavingGoal(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(savingGoalService.lastAddedSavingGoal).isEqualTo(goal);
    }

    @Test
    void deleteSavingGoal_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.deleteSavingGoal(goalId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(savingGoalService.lastDeletedId).isEqualTo(goalId);
    }

    private static final class StubSavingGoalService extends SavingGoalService {

        private Optional<SavingGoal> findByIdResult = Optional.empty();
        private List<SavingGoal> findAllResult = List.of();
        private SavingGoal addResult;
        private SavingGoal updateResult;
        private SavingGoal lastAddedSavingGoal;
        private UUID lastDeletedId;

        private StubSavingGoalService() {
            super(null);
        }

        @Override
        public Optional<SavingGoal> findById(UUID id) {
            return findByIdResult;
        }

        @Override
        public List<SavingGoal> findAll() {
            return findAllResult;
        }

        @Override
        public SavingGoal addSavingGoal(SavingGoal savingGoal) {
            lastAddedSavingGoal = savingGoal;
            return addResult;
        }

        @Override
        public SavingGoal updateSavingGoal(UUID goalId, SavingGoal savingGoal) {
            return updateResult;
        }

        @Override
        public void deleteSavingGoalById(UUID goalId) {
            lastDeletedId = goalId;
        }
    }
}
