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
        titular = new Titular(titularId, "Ana", "Lopez", "Garcia",
                "3109876543", Instant.now(), "COP", "America/Bogota", "token-abc");
    }

    @Nested
    @DisplayName("HU-05 - Crear una categoria personalizada")
    class CrearCategoria {

        @Test
        @DisplayName("CA-01 CA-04 - Crear categoria Transporte exitosamente")
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

            List<Category> listado = categoryService.findAll();
            assertThat(listado).extracting(Category::nombre).contains("Transporte");

            verify(categoryRepositoryPort).save(any(Category.class));
        }

        @Test
        @DisplayName("CA-02 - Nombre duplicado lanza CategoryAlreadyExistsException")
        void ca02_nombreDuplicadoLanzaExcepcion() {
            Category duplicada = new Category(null, "Alimentacion", titular);

            when(categoryRepositoryPort.existsByNameAndTitularId("Alimentacion", titularId)).thenReturn(true);

            assertThatThrownBy(() -> categoryService.addCategory(duplicada))
                    .isInstanceOf(CategoryAlreadyExistsException.class)
                    .hasMessageContaining("Alimentacion");

            verify(categoryRepositoryPort, never()).save(any());
        }

        @ParameterizedTest(name = "CA-03 | nombre en blanco=''{0}''")
        @ValueSource(strings = {"", "   "})
        @DisplayName("CA-03 - Nombre vacio o en blanco no crea categoria")
        void ca03_nombreVacioNoCrearCategoria(String nombreVacio) {
            Category invalida = new Category(null, nombreVacio, titular);

            when(categoryRepositoryPort.existsByNameAndTitularId(nombreVacio, titularId)).thenReturn(false);
            when(categoryRepositoryPort.save(any()))
                    .thenThrow(new IllegalArgumentException("El nombre de la categoria es obligatorio"));

            assertThatThrownBy(() -> categoryService.addCategory(invalida))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nombre");
        }

        @Test
        @DisplayName("Actualizar categoria inexistente lanza ResourceNotFoundException")
        void actualizarCategoriaNoExistente() {
            UUID idFalso = UUID.randomUUID();
            Category cat = new Category(null, "Nueva", titular);

            when(categoryRepositoryPort.findById(idFalso)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.updateCategory(idFalso, cat))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("La categoria no fue encontrada");

            verify(categoryRepositoryPort, never()).update(any(), any());
        }

        @Test
        @DisplayName("Eliminar categoria en uso lanza CategoryInUseException")
        void eliminarCategoriaEnUso() {
            UUID categoriaId = UUID.randomUUID();
            Category catExistente = new Category(categoriaId, "Alimentacion", titular);

            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(catExistente));
            when(transactionRepositoryPort.existsByCategoryId(categoriaId)).thenReturn(true);

            assertThatThrownBy(() -> categoryService.deleteCategoryById(categoriaId))
                    .isInstanceOf(com.example.demo.domain.exception.CategoryInUseException.class);

            verify(categoryRepositoryPort, never()).deleteById(any());
        }

        @Test
        @DisplayName("findById - retorna categoria existente")
        void findById_retornaCategoriaExistente() {
            UUID categoriaId = UUID.randomUUID();
            Category cat = new Category(categoriaId, "Salud", titular);

            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(cat));

            Optional<Category> resultado = categoryService.findById(categoriaId);

            assertThat(resultado).hasValueSatisfying(c ->
                assertThat(c.nombre()).isEqualTo("Salud")
            );
        }

        @Test
        @DisplayName("findById - retorna vacio si no existe")
        void findById_retornaVacioSiNoExiste() {
            UUID idFalso = UUID.randomUUID();
            when(categoryRepositoryPort.findById(idFalso)).thenReturn(Optional.empty());

            Optional<Category> resultado = categoryService.findById(idFalso);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("updateCategory - actualiza categoria exitosamente")
        void updateCategory_exitoso() {
            UUID categoriaId = UUID.randomUUID();
            Category existente = new Category(categoriaId, "Viejo", titular);
            Category actualizada = new Category(categoriaId, "Nuevo", titular);

            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(existente));
            when(categoryRepositoryPort.existsByNameAndTitularId("Nuevo", titularId)).thenReturn(false);
            when(categoryRepositoryPort.update(categoriaId, actualizada)).thenReturn(actualizada);

            Category resultado = categoryService.updateCategory(categoriaId, actualizada);

            assertThat(resultado.nombre()).isEqualTo("Nuevo");
            verify(categoryRepositoryPort).update(categoriaId, actualizada);
        }

        @Test
        @DisplayName("updateCategory - nombre duplicado lanza CategoryAlreadyExistsException")
        void updateCategory_nombreDuplicado() {
            UUID categoriaId = UUID.randomUUID();
            Category existente = new Category(categoriaId, "Viejo", titular);
            Category actualizada = new Category(categoriaId, "Duplicado", titular);

            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(existente));
            when(categoryRepositoryPort.existsByNameAndTitularId("Duplicado", titularId)).thenReturn(true);

            assertThatThrownBy(() -> categoryService.updateCategory(categoriaId, actualizada))
                    .isInstanceOf(CategoryAlreadyExistsException.class);

            verify(categoryRepositoryPort, never()).update(any(), any());
        }

        @Test
        @DisplayName("deleteCategoryById - elimina categoria exitosamente")
        void deleteCategoryById_exitoso() {
            UUID categoriaId = UUID.randomUUID();
            Category cat = new Category(categoriaId, "Entretenimiento", titular);

            when(categoryRepositoryPort.findById(categoriaId)).thenReturn(Optional.of(cat));
            when(transactionRepositoryPort.existsByCategoryId(categoriaId)).thenReturn(false);

            categoryService.deleteCategoryById(categoriaId);

            verify(categoryRepositoryPort).deleteById(categoriaId);
        }

        @Test
        @DisplayName("deleteCategoryById - categoria inexistente lanza ResourceNotFoundException")
        void deleteCategoryById_noExistente() {
            UUID idFalso = UUID.randomUUID();
            when(categoryRepositoryPort.findById(idFalso)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.deleteCategoryById(idFalso))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(categoryRepositoryPort, never()).deleteById(any());
        }
    }
}
