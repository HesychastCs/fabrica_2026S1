package com.example.demo.infra.persistence.repository;

import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.TitularEntityMapper;
import com.example.demo.infra.persistence.entity.TitularEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JpaTitularRepositoryAdapter")
class JpaTitularRepositoryAdapterTest {

    @Mock private JpaTitularRepository jpaTitularRepository;
    @Mock private TitularEntityMapper titularEntityMapper;

    @InjectMocks private JpaTitularRepositoryAdapter adapter;

    private UUID titularId;
    private Titular titular;
    private TitularEntity entity;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        titular = new Titular(titularId, "Carlos", "Zuluaga", "Amaya",
                "3001112233", Instant.now(), "COP", "America/Bogota", "tkn-xyz");
        entity = new TitularEntity();
        entity.setTitularId(titularId);
        entity.setNombre("Carlos");
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("retorna dominio cuando el titular existe")
        void findById_existing_returnsMappedDomain() {
            when(jpaTitularRepository.findById(titularId)).thenReturn(Optional.of(entity));
            when(titularEntityMapper.toDomain(entity)).thenReturn(titular);

            Optional<Titular> result = adapter.findById(titularId);

            assertThat(result).isPresent().contains(titular);
            verify(titularEntityMapper).toDomain(entity);
        }

        @Test
        @DisplayName("retorna vacío cuando el titular no existe")
        void findById_missing_returnsEmpty() {
            when(jpaTitularRepository.findById(titularId)).thenReturn(Optional.empty());

            Optional<Titular> result = adapter.findById(titularId);

            assertThat(result).isEmpty();
            verifyNoInteractions(titularEntityMapper);
        }
    }
}
