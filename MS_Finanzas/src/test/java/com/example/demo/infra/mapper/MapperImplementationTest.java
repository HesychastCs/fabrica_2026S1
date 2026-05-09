package com.example.demo.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.Report;
import com.example.demo.domain.model.SavingGoal;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.persistence.entity.CategoryEntity;
import com.example.demo.infra.persistence.entity.ReportEntity;
import com.example.demo.infra.persistence.entity.TitularEntity;
import com.example.demo.infra.persistence.entity.TransactionEntity;
import com.example.demo.infra.rest.dto.SavingGoalRequest;
import com.example.demo.infra.rest.dto.TransactionRequest;
import com.example.demo.infra.rest.dto.SavingGoalResponse;

class MapperImplementationTest {

    @Test
    void transactionEntityMapper_shouldMapDomainToEntityAndBack() {
        TransactionEntityMapper mapper = Mappers.getMapper(TransactionEntityMapper.class);
        UUID categoryId = UUID.randomUUID();
        UUID titularId = UUID.randomUUID();

        Category category = new Category(categoryId, "Alimentación", null);
        Titular titular = new Titular(titularId, "Ana", "Lopez", "Garcia", "3001234567", Instant.now(), "COP", "America/Bogota", "token-1");
        Transaction transaction = new Transaction(UUID.randomUUID(), "Pago luz", "Pago de energía", BigDecimal.valueOf(150000), TypeTransaction.GASTO, LocalDate.of(2026, 5, 1), category, titular);

        TransactionEntity entity = mapper.toEntity(transaction);

        assertThat(entity).isNotNull();
        assertThat(entity.getTransactionId()).isEqualTo(transaction.transactionId());
        assertThat(entity.getNombre()).isEqualTo(transaction.nombre());
        assertThat(entity.getDescripcion()).isEqualTo(transaction.descripcion());
        assertThat(entity.getMonto()).isEqualTo(transaction.monto());
        assertThat(entity.getTipo()).isEqualTo(transaction.tipo());
        assertThat(entity.getFecha()).isEqualTo(transaction.fecha());
        assertThat(entity.getCategoria()).isNull();
        assertThat(entity.getTitular()).isNull();

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCategoriaId(categoryId);
        categoryEntity.setNombre(category.nombre());

        TitularEntity titularEntity = new TitularEntity();
        titularEntity.setTitularId(titularId);
        titularEntity.setNombre(titular.nombre());
        titularEntity.setPrimerApellido(titular.primerApellido());
        titularEntity.setSegundoApellido(titular.segundoApellido());
        titularEntity.setTelefono(titular.telefono());
        titularEntity.setToken(titular.token());
        titularEntity.setZonaHoraria(titular.zonaHoraria());
        titularEntity.setMonedaPreferida(titular.monedaPreferida());
        titularEntity.setFechaRegistro(titular.fechaRegistro());

        TransactionEntity entityWithRelations = new TransactionEntity();
        entityWithRelations.setTransactionId(transaction.transactionId());
        entityWithRelations.setNombre(transaction.nombre());
        entityWithRelations.setDescripcion(transaction.descripcion());
        entityWithRelations.setMonto(transaction.monto());
        entityWithRelations.setTipo(transaction.tipo());
        entityWithRelations.setFecha(transaction.fecha());
        entityWithRelations.setCategoria(categoryEntity);
        entityWithRelations.setTitular(titularEntity);

        Transaction mappedDomain = mapper.toDomain(entityWithRelations);

        assertThat(mappedDomain.transactionId()).isEqualTo(transaction.transactionId());
        assertThat(mappedDomain.nombre()).isEqualTo(transaction.nombre());
        assertThat(mappedDomain.categoria()).isNotNull();
        assertThat(mappedDomain.categoria().categoriaId()).isEqualTo(categoryId);
        assertThat(mappedDomain.titular()).isNotNull();
        assertThat(mappedDomain.titular().titularId()).isEqualTo(titularId);
    }

    @Test
    void transactionRequestMapper_shouldMapRequestAndBack() {
        TransactionRequestMapper mapper = Mappers.getMapper(TransactionRequestMapper.class);
        UUID categoryId = UUID.randomUUID();
        UUID titularId = UUID.randomUUID();

        TransactionRequest request = new TransactionRequest(
            "Pago agua",
            BigDecimal.valueOf(80000),
            "Servicio",
            TypeTransaction.GASTO,
            LocalDate.of(2026, 5, 2),
            categoryId.toString(),
            titularId.toString()
        );

        Transaction transaction = mapper.toDomain(request);

        assertThat(transaction.transactionId()).isNull();
        assertThat(transaction.nombre()).isEqualTo(request.nombre());
        assertThat(transaction.categoria()).isNotNull();
        assertThat(transaction.categoria().categoriaId()).isEqualTo(categoryId);
        assertThat(transaction.titular()).isNotNull();
        assertThat(transaction.titular().titularId()).isEqualTo(titularId);

        TransactionRequest reconstructed = mapper.toRequest(transaction);

        assertThat(reconstructed.categoriaId()).isEqualTo(categoryId.toString());
        assertThat(reconstructed.titularId()).isEqualTo(titularId.toString());
    }

    @Test
    void savingGoalRequestMapper_toDomainAndCreateTitularValidation() {
        SavingGoalRequestMapper mapper = Mappers.getMapper(SavingGoalRequestMapper.class);
        UUID titularId = UUID.randomUUID();

        SavingGoalRequest request = new SavingGoalRequest("Vacaciones", 1000000.0, LocalDate.of(2026, 12, 31), titularId);
        SavingGoal goal = mapper.toDomain(request);

        assertThat(goal.goalId()).isNull();
        assertThat(goal.nombre()).isEqualTo(request.nombre());
        assertThat(goal.titular()).isNotNull();
        assertThat(goal.titular().titularId()).isEqualTo(titularId);

        assertThatThrownBy(() -> mapper.createTitular(new SavingGoalRequest("Vacaciones", 1000000.0, LocalDate.now(), null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("titularId");
    }

    @Test
    void savingGoalResponseMapper_toResponse_shouldMapValueAndEstado() {
        SavingGoalResponseMapper mapper = Mappers.getMapper(SavingGoalResponseMapper.class);
        UUID titularId = UUID.randomUUID();
        Titular titular = new Titular(titularId, "Ana", "Lopez", "Garcia", "3001234567", Instant.now(), "COP", "America/Bogota", "token-1");
        SavingGoal goal = new SavingGoal(UUID.randomUUID(), "Viaje", 1200000.0, 300000, GoalStatus.EN_PROGRESO, LocalDate.of(2026, 12, 31), titular);

        SavingGoalResponse response = mapper.toResponse(goal);

        assertThat(response.getGoalId()).isEqualTo(goal.goalId());
        assertThat(response.getNombre()).isEqualTo(goal.nombre());
        assertThat(response.getAvance()).isEqualTo(Double.valueOf(goal.avance()));
        assertThat(response.getEstado()).isEqualTo(goal.estado().name());
        assertThat(response.getTitularId()).isEqualTo(titularId);
        assertThat(response.getTitularNombre()).isEqualTo(titular.nombre());
    }

    @Test
    void categoryEntityMapper_shouldMapDomainToEntityAndBack() {
        CategoryEntityMapper mapper = Mappers.getMapper(CategoryEntityMapper.class);
        UUID categoryId = UUID.randomUUID();
        UUID titularId = UUID.randomUUID();
        Titular titular = new Titular(titularId, "Ana", "Lopez", "Garcia", "3001234567", Instant.now(), "COP", "America/Bogota", "token-1");
        Category category = new Category(categoryId, "Alimentación", titular);

        CategoryEntity entity = mapper.toEntity(category);

        assertThat(entity).isNotNull();
        assertThat(entity.getNombre()).isEqualTo(category.nombre());
        assertThat(entity.getCategoriaId()).isEqualTo(category.categoriaId());
        assertThat(entity.getTitular()).isNotNull();
        assertThat(entity.getTitular().getTitularId()).isEqualTo(titularId);
        assertThat(entity.getTransacciones()).isNull();

        CategoryEntity persisted = new CategoryEntity();
        persisted.setCategoriaId(categoryId);
        persisted.setNombre(category.nombre());
        persisted.setTitular(entity.getTitular());

        Category mapped = mapper.toDomain(persisted);

        assertThat(mapped.categoriaId()).isEqualTo(categoryId);
        assertThat(mapped.nombre()).isEqualTo(category.nombre());
        assertThat(mapped.titular()).isNotNull();
        assertThat(mapped.titular().titularId()).isEqualTo(titularId);
    }

    @Test
    void reportEntityMapper_shouldMapBetweenDomainAndEntity() {
        ReportEntityMapper mapper = Mappers.getMapper(ReportEntityMapper.class);
        UUID titularId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();

        TitularEntity titularEntity = new TitularEntity();
        titularEntity.setTitularId(titularId);
        titularEntity.setNombre("Pedro");
        titularEntity.setToken("token");
        titularEntity.setZonaHoraria("America/Bogota");
        titularEntity.setMonedaPreferida("COP");
        titularEntity.setFechaRegistro(Instant.now());

        ReportEntity entity = new ReportEntity();
        entity.setReportId(reportId);
        entity.setMes(5);
        entity.setAnho(2026);
        entity.setIngresosAcumulados(BigDecimal.valueOf(100000));
        entity.setGastosAcumulados(BigDecimal.valueOf(50000));
        entity.setAportesMetaAcumulados(BigDecimal.valueOf(25000));
        entity.setBalanceNeto(BigDecimal.valueOf(25000));
        entity.setFechaGenerado(Instant.now());
        entity.setTitular(titularEntity);

        Report report = mapper.toDomain(entity, titularEntity);

        assertThat(report).isNotNull();
        assertThat(report.reportId()).isEqualTo(reportId);
        assertThat(report.mes()).isEqualTo(5);
        assertThat(report.anho()).isEqualTo(2026);
        assertThat(report.titular()).isNotNull();
        assertThat(report.titular().titularId()).isEqualTo(titularId);

        ReportEntity mappedEntity = mapper.toEntity(report);

        assertThat(mappedEntity).isNotNull();
        assertThat(mappedEntity.getMes()).isEqualTo(report.mes());
        assertThat(mappedEntity.getAnho()).isEqualTo(report.anho());
        assertThat(mappedEntity.getBalanceNeto()).isEqualTo(report.balanceNeto());
    }
}
