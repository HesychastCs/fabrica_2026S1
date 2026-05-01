package com.example.demo.domain.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String categoryName) {
        super("La categoria " + categoryName + " ya existe");
    }

}
