package com.example.reportes.domain.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ApiError(request.getRequestURI(), ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now())
        );
    }

    @ExceptionHandler(NoTransactionsInMonthException.class)
    public ResponseEntity<ApiError> handleNoTransactions(NoTransactionsInMonthException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ApiError(request.getRequestURI(), ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
            errors.put(fe.getField(), fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "inválido")
        );
        return ResponseEntity.badRequest().body(
            new ApiError(
                request.getRequestURI(),
                "Error de validación",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                errors
            )
        );
    }
}
