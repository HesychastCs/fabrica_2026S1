package com.example.demo.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.rest.dto.SavingGoalRequest;
import org.junit.jupiter.api.Test;

class MapperDefaultMethodsTest {

    @Test
    void transactionRequestMapper_shouldConvertCategoryAndTitularStrings() {
        TransactionRequestMapper mapper = new TransactionRequestMapper() {
            @Override
            public Transaction toDomain(com.example.demo.infra.rest.dto.TransactionRequest request) {
                return null;
            }
        };
        Transaction transaction = new Transaction(
            UUID.randomUUID(),
            "Pago prueba",
            "Descripción",
            java.math.BigDecimal.valueOf(120000),
            TypeTransaction.GASTO,
            java.time.LocalDate.of(2026, 5, 1),
            new Category(UUID.randomUUID(), "Salud", new Titular(UUID.randomUUID(), "Ana", "Lopez", "Garcia", "3001234567", java.time.Instant.now(), "COP", "America/Bogota", "token-1")),
            new Titular(UUID.randomUUID(), "Ana", "Lopez", "Garcia", "3001234567", java.time.Instant.now(), "COP", "America/Bogota", "token-1")
        );

        com.example.demo.infra.rest.dto.TransactionRequest request = mapper.toRequest(transaction);

        assertThat(request).isNotNull();
        assertThat(request.categoriaId()).isEqualTo(transaction.categoria().categoriaId().toString());
        assertThat(request.titularId()).isEqualTo(transaction.titular().titularId().toString());
    }

    @Test
    void transactionRequestMapper_mapCategory_returnsNullForBlank() {
        TransactionRequestMapper mapper = new TransactionRequestMapper() {
            @Override
            public Transaction toDomain(com.example.demo.infra.rest.dto.TransactionRequest request) {
                return null;
            }
        };

        assertThat(mapper.mapCategory(" ")).isNull();
    }

    @Test
    void transactionRequestMapper_mapTitular_returnsTitular() {
        TransactionRequestMapper mapper = new TransactionRequestMapper() {
            @Override
            public Transaction toDomain(com.example.demo.infra.rest.dto.TransactionRequest request) {
                return null;
            }
        };
        UUID titularId = UUID.randomUUID();

        Titular titular = mapper.mapTitular(titularId.toString());

        assertThat(titular).isNotNull();
        assertThat(titular.titularId()).isEqualTo(titularId);
    }

    @Test
    void reportRequestMapper_mapTitularId_returnsTitularOrNull() {
        ReportRequestMapper mapper = new ReportRequestMapper() {
            @Override
            public com.example.demo.domain.model.Report toDomain(com.example.demo.infra.rest.dto.ReportRequest reportRequest) {
                return null;
            }
        };
        UUID titularId = UUID.randomUUID();

        assertThat(mapper.mapTitularId(null)).isNull();
        assertThat(mapper.mapTitularId(titularId).titularId()).isEqualTo(titularId);
    }

   @Test
void savingGoalRequestMapper_createTitular_requiresTitularId() {
    SavingGoalRequestMapper mapper = new SavingGoalRequestMapper() {
        @Override
        public com.example.demo.domain.model.SavingGoal toDomain(SavingGoalRequest request) {
            return null;
        }
    };

    SavingGoalRequest request = new SavingGoalRequest("Vacaciones", 1000.0, java.time.LocalDate.now(), null);
    assertThatThrownBy(() -> mapper.createTitular(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("titularId");
}

    @Test
    void transactionResponseMapper_stringToUuid_handlesNullAndValidValues() {
        TransactionResponseMapper mapper = new TransactionResponseMapper() {
            @Override
            public com.example.demo.infra.rest.dto.TransactionResponse toResponse(Transaction transaction) {
                return null;
            }
        };
        UUID expected = UUID.randomUUID();

        assertThat(mapper.stringToUuid(null)).isNull();
        assertThat(mapper.stringToUuid(expected.toString())).isEqualTo(expected);
    }
}
