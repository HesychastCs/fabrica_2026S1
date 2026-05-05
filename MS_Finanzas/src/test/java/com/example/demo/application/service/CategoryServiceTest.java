package com.example.demo.application.service;

import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.domain.exception.CategoryAlreadyExistsException;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.Titular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para CategoryService
 * Cubre HU-05 — Crear una categoría personalizada
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepositoryPort categoryRepositoryPort;
    @Mock private TransactionRepositoryPort transactionRepositoryPort;

    @InjectMocks private CategoryService categoryService;

    private Titular titular;
    private UUID titularId;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        titular = new Titular(titularId, "Ana", "López", "García",
                "3109876543", Instant.now(), "COP", "America/Bogota", "token-abc");
    }

    // ─────────────────────────────────────────────────────────────────
    // HU-05 — Crear una categoría personalizada
    // ─────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("HU-05 — Crear una categoría personalizada")
    class CrearCategoria {

        /**
         * CA-01 + CA-04 @happy-path
         * Crear categoría exitosamente → aparece en listado y disponible al registrar transacciones.
         */
        @Test
        @DisplayName("CA-01 CA-04 — Crear categoría 'Transporte' exitosamente y disponible en listado")
        void ca01_ca04_crearCategoriaExitosa() {
            Category nuevaCategoria = new Category(null, "Transporte", titular);
            Category guardada = new Category(UUID.randomUUID(), "Transporte", titular);

            when(categoryRepositoryPort.existsByNameAndTitularId("Transporte", titularId)).thenReturn(false);
            when(categoryRepositoryPort.save(any())).thenReturn(guardada);
            when(categoryRepositoryPort.findAll()).thenReturn(List.of(guardada));

            Category resultado = categoryService.addCategory(nuevaCategoria);

            assertThat(resultado).isNotNull();
            assertThat(resultado.categoriaId()).isNotNull();
            assertThat(resultado.nombre()).isEqualTo("Transporte");

            // Verificar disponibilidad en el listado
            List<Category> listado = categoryService.findAll();
            assertThat(listado).extracting(Category::nombre).contains("Transporte");

            verify(categoryRepositoryPort).save(any(Category.class));
        }

        /**
         * CA-02 @error-handling
         * Nombre duplicado → lanza CategoryAlreadyExistsException, no crea categoría.
         */
        @Test
        @DisplayName("CA-02 — Nombre duplicado lanza CategoryAlreadyExistsException")
        void ca02_nombreDuplicadoLanzaExcepcion() {
            Category duplicada = new Category(null, "Alimentación", titular);

            when(categoryRepositoryPort.existsByNameAndTitularId("Alimentación", titularId)).thenReturn(true);

            assertThatThrownBy(() -> categoryService.addCategory(duplicada))
                    .isInstanceOf(CategoryAlreadyExistsException.class)
                    .hasMessageContaining("Alimentación");

            verify(categoryRepositoryPort, never()).save(any());
        }

        /**
         * CA-03 @error-handling
         * Nombre vacío o en blanco → No llega al repositorio (validación Bean Validation en controller).
         * En el servicio verificamos que si llegara, existsByNameAndTitularId retorna false y
         * el save no se invoca con nombre vacío (la validación real es @NotBlank en el DTO).
         * Aquí simulamos que el repositorio rechaza un nombre en blanco.
         */
        @ParameterizedTest(name = "CA-03 | nombre en blanco=''{0}''")
        @ValueSource(strings = {"", "   "})
        @DisplayName("CA-03 — Nombre vacío o en blanco no crea categoría")
        void ca03_nombreVacioNoCrearCategoria(String nombreVacio) {
            Category invalida = new Category(null, nombreVacio, titular);

            // existsByNameAndTitularId se llama con nombre vacío/blanco: retorna false
            when(categoryRepositoryPort.existsByNameAndTitularId(nombreVacio, titularId)).thenReturn(false);
            // El repositorio lanza excepción al intentar guardar nombre inválido
            when(categoryRepositoryPort.save(any()))
                    .thenThrow(new IllegalArgumentException("El nombre de la categoría es obligatorio"));

            assertThatThrownBy(() -> categoryService.addCategory(invalida))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nombre");
        }

        /**
         * Flujo de actualización — categoría no encontrada → ResourceNotFoundException.
         */
        @Test
        @DisplayName("Actualizar categoría inexistente lanza ResourceNotFoundException")
        void actualizarCategoriaNoExistente() {
            UUID idFalso = UUID.randomUUID();
            Category cat = new Category(null, "Nueva", titular);

            when(categoryRepositoryPort.findById(idFalso)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.updateCategory(idFalso, cat))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("La categoria no fue encontrada");

            verify(categoryRepositoryPort, never()).update(any(), any());
        }

        /**
         * Eliminar categoría en uso → lanza CategoryInUseException.
         */
        @Test
        @DisplayName("Eliminar categoría en uso lanza CategoryInUseException")
        void eliminarCategoriaEnUso() {
            UUID categoriaId = UUID.randomUUID();
            Category catExistente = new Category(categoriaId, "Alimentación", titular);

            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(catExistente));
            when(transactionRepositoryPort.existsByCategoryId(categoriaId)).thenReturn(true);

            assertThatThrownBy(() -> categoryService.deleteCategoryById(categoriaId))
                    .isInstanceOf(com.example.demo.domain.exception.CategoryInUseException.class);

            verify(categoryRepositoryPort, never()).deleteById(any());
        }
    }
}
