package com.example.demo.domain.exception;

public class DuplicateGoalNameException extends RuntimeException {

    public DuplicateGoalNameException(String message) {
        super(message);
    }

    public DuplicateGoalNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
