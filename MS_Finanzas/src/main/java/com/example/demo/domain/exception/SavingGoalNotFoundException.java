package com.example.demo.domain.exception;

public class SavingGoalNotFoundException extends RuntimeException {

    public SavingGoalNotFoundException(String message) {
        super(message);
    }
}
