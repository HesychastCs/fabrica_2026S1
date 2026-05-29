package com.example.demo.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain exceptions — constructores y mensajes")
class DomainExceptionsTest {

    @Test
    @DisplayName("DuplicateGoalNameException(String, Throwable) preserva mensaje y causa")
    void duplicateGoalNameException_withCause_preservesMessageAndCause() {
        Throwable cause = new RuntimeException("causa original");
        DuplicateGoalNameException ex = new DuplicateGoalNameException("Meta duplicada", cause);

        assertThat(ex.getMessage()).isEqualTo("Meta duplicada");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("DuplicateGoalNameException(String) contiene el mensaje dado")
    void duplicateGoalNameException_stringOnly_containsMessage() {
        DuplicateGoalNameException ex = new DuplicateGoalNameException("Nombre repetido");
        assertThat(ex.getMessage()).isEqualTo("Nombre repetido");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("CategoryAlreadyExistsException incluye nombre de categoría")
    void categoryAlreadyExistsException_includesCategoryName() {
        CategoryAlreadyExistsException ex = new CategoryAlreadyExistsException("Alimentación");
        assertThat(ex.getMessage()).contains("Alimentación");
    }

    @Test
    @DisplayName("CategoryInUseException incluye ID de categoría")
    void categoryInUseException_includesCategoryId() {
        CategoryInUseException ex = new CategoryInUseException("abc-123");
        assertThat(ex.getMessage()).contains("abc-123");
    }

    @Test
    @DisplayName("NoTransactionsInMonthException incluye mes y año")
    void noTransactionsInMonthException_includesMonthAndYear() {
        NoTransactionsInMonthException ex = new NoTransactionsInMonthException(5, 2025);
        assertThat(ex.getMessage()).contains("5").contains("2025");
    }

    @Test
    @DisplayName("ResourceNotFoundException conserva el mensaje")
    void resourceNotFoundException_preservesMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso no hallado");
        assertThat(ex.getMessage()).isEqualTo("Recurso no hallado");
    }

    @Test
    @DisplayName("SavingGoalNotFoundException conserva el mensaje")
    void savingGoalNotFoundException_preservesMessage() {
        SavingGoalNotFoundException ex = new SavingGoalNotFoundException("Meta no encontrada");
        assertThat(ex.getMessage()).isEqualTo("Meta no encontrada");
    }
}
