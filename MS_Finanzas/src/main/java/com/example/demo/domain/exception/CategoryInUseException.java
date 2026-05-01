package com.example.demo.domain.exception;

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(String categoryId) {
        super("No se puede eliminar la categoria con id " + categoryId + " porque tiene transacciones asociadas");
    }

}
