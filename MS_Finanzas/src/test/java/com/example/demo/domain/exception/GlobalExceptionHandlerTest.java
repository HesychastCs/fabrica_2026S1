package com.example.demo.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundException_shouldReturnNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/categories/123");

        ResponseEntity<ApiError> response = handler.handleResourceNotFoundException(new ResourceNotFoundException("No existe"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("No existe");
        assertThat(response.getBody().path()).isEqualTo("/api/categories/123");
    }

    @Test
    void handleValidationExceptions_shouldReturnBadRequestWithFieldMessages() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/transactions");
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "transactionRequest");
        FieldError fieldError = new FieldError("transactionRequest", "monto", "El monto es obligatorio");
        bindingResult.addError(fieldError);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiError> response = handler.handleValidationExceptions(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Error de validación");
        assertThat(response.getBody().fieldErrors()).containsEntry("monto", "El monto es obligatorio");
    }

    @Test
    void handleCategoryInUseException_shouldReturnConflict() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/categories/123");

        ResponseEntity<ApiError> response = handler.handleCategoryInUseException(new CategoryInUseException("En uso"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("En uso");
    }

    @Test
    void handleCategoryAlreadyExistsException_shouldReturnConflict() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/categories");

        ResponseEntity<ApiError> response = handler.handleCategoryAlreadyExistsException(new CategoryAlreadyExistsException("Duplicada"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("Duplicada");
    }

    @Test
    void handleNoTransactionsInMonthException_shouldReturnNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/reports/month");

        ResponseEntity<ApiError> response = handler.handleNoTransactionsInMonthException(new NoTransactionsInMonthException(5, 2026), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void handleException_shouldReturnInternalServerError() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/any");

        ResponseEntity<ApiError> response = handler.handleException(new RuntimeException("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("boom");
    }
}
