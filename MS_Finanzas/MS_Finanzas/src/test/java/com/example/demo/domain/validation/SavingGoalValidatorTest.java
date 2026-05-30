package com.example.demo.domain.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class SavingGoalValidatorTest {

    @Test
    void validateMonto_shouldAcceptPositive() {
        assertDoesNotThrow(() -> SavingGoalValidator.validateMonto(1.0));
    }

    @Test
    void validateMonto_shouldRejectZero() {
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateMonto(0.0));
    }
}
