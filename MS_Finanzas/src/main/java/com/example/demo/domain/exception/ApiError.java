package com.example.demo.domain.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
    String path,
    String message,
    int statusCode,
    LocalDateTime timestamp,
    Map<String, String> fieldErrors
) {

    public ApiError(String path, String message, int statusCode, LocalDateTime timestamp) {
        this(path, message, statusCode, timestamp, Map.of());
    }
}
