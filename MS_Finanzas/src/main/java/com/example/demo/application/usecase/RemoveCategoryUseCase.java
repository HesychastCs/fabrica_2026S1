package com.example.demo.application.usecase;

import java.util.UUID;

public interface RemoveCategoryUseCase {
    void deleteCategoryById(UUID categoryId);
}
