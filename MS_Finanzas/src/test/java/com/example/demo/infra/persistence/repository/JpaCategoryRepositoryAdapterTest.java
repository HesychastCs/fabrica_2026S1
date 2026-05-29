package com.example.demo.infra.persistence.repository;

import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.CategoryEntityMapper;
import com.example.demo.infra.persistence.entity.CategoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JpaCategoryRepositoryAdapter")
class JpaCategoryRepositoryAdapterTest {

    @Mock private JpaCategoryRepository jpaCategoryRepository;
    @Mock private CategoryEntityMapper categoryEntityMapper;

    @InjectMocks private JpaCategoryRepositoryAdapter adapter;

    private UUID categoriaId;
    private UUID titularId;
    private Titular titular;
    private Category category;
    private CategoryEntity entity;

    @BeforeEach
    void setUp() {
        categoriaId = UUID.randomUUID();
        titularId = UUID.randomUUID();
        titular = new Titular(titularId, "Ana", "Lopez", "Garcia",
                "3001234567", Instant.now(), "COP", "America/Bogota", "tkn");
        category = new Category(categoriaId, "Alimentación", titular);
        entity = new CategoryEntity();
        entity.setCategoriaId(categoriaId);
        entity.setNombre("Alimentación");
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("retorna dominio cuando existe")
        void findById_existing_returnsDomain() {
            when(jpaCategoryRepository.findById(categoriaId)).thenReturn(Optional.of(entity));
            when(categoryEntityMapper.toDomain(entity)).thenReturn(category);

            Optional<Category> result = adapter.findById(categoriaId);

            assertThat(result).isPresent().contains(category);
        }

        @Test
        @DisplayName("retorna vacío cuando no existe")
        void findById_missing_returnsEmpty() {
            when(jpaCategoryRepository.findById(categoriaId)).thenReturn(Optional.empty());

            assertThat(adapter.findById(categoriaId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("retorna lista mapeada")
        void findAll_returnsMappedList() {
            when(jpaCategoryRepository.findAll()).thenReturn(List.of(entity));
            when(categoryEntityMapper.toDomain(entity)).thenReturn(category);

            List<Category> result = adapter.findAll();

            assertThat(result).hasSize(1).contains(category);
        }

        @Test
        @DisplayName("retorna lista vacía si no hay categorías")
        void findAll_empty_returnsEmptyList() {
            when(jpaCategoryRepository.findAll()).thenReturn(List.of());

            assertThat(adapter.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("guarda y retorna dominio mapeado")
        void save_persistsAndReturnsDomain() {
            when(categoryEntityMapper.toEntity(category)).thenReturn(entity);
            when(jpaCategoryRepository.save(entity)).thenReturn(entity);
            when(categoryEntityMapper.toDomain(entity)).thenReturn(category);

            Category result = adapter.save(category);

            assertThat(result).isEqualTo(category);
            verify(jpaCategoryRepository).save(entity);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("actualiza nombre y retorna dominio")
        void update_existing_updatesNombreAndReturns() {
            Category updated = new Category(categoriaId, "Transporte", titular);
            when(jpaCategoryRepository.findById(categoriaId)).thenReturn(Optional.of(entity));
            when(jpaCategoryRepository.save(entity)).thenReturn(entity);
            when(categoryEntityMapper.toDomain(entity)).thenReturn(updated);

            Category result = adapter.update(categoriaId, updated);

            assertThat(result).isEqualTo(updated);
            assertThat(entity.getNombre()).isEqualTo("Transporte");
        }

        @Test
        @DisplayName("lanza ResourceNotFoundException si no existe")
        void update_missing_throwsResourceNotFoundException() {
            when(jpaCategoryRepository.findById(categoriaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adapter.update(categoriaId, category))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {

        @Test
        @DisplayName("delega al repositorio JPA")
        void deleteById_delegatesToJpa() {
            adapter.deleteById(categoriaId);
            verify(jpaCategoryRepository).deleteById(categoriaId);
        }
    }

    @Nested
    @DisplayName("existsByNameAndTitularId")
    class ExistsByNameAndTitularId {

        @Test
        @DisplayName("retorna true cuando existe")
        void existsByNameAndTitularId_existing_returnsTrue() {
            when(jpaCategoryRepository.existsByNombreIgnoreCaseAndTitular_TitularId("Alimentación", titularId))
                    .thenReturn(true);

            assertThat(adapter.existsByNameAndTitularId("Alimentación", titularId)).isTrue();
        }

        @Test
        @DisplayName("retorna false cuando no existe")
        void existsByNameAndTitularId_missing_returnsFalse() {
            when(jpaCategoryRepository.existsByNombreIgnoreCaseAndTitular_TitularId("X", titularId))
                    .thenReturn(false);

            assertThat(adapter.existsByNameAndTitularId("X", titularId)).isFalse();
        }
    }

    @Nested
    @DisplayName("findByNombreIgnoreCase")
    class FindByNombreIgnoreCase {

        @Test
        @DisplayName("retorna dominio cuando existe")
        void findByNombreIgnoreCase_existing_returnsDomain() {
            when(jpaCategoryRepository.findByNombreIgnoreCase("alimentación")).thenReturn(Optional.of(entity));
            when(categoryEntityMapper.toDomain(entity)).thenReturn(category);

            assertThat(adapter.findByNombreIgnoreCase("alimentación")).isPresent().contains(category);
        }

        @Test
        @DisplayName("retorna vacío cuando no existe")
        void findByNombreIgnoreCase_missing_returnsEmpty() {
            when(jpaCategoryRepository.findByNombreIgnoreCase("X")).thenReturn(Optional.empty());

            assertThat(adapter.findByNombreIgnoreCase("X")).isEmpty();
        }
    }
}
