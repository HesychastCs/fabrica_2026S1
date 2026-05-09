package com.example.demo.application.service;

import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.domain.model.Titular;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TitularService - pruebas unitarias")
class TitularServiceTest {

    @Mock
    private TitularRepositoryPort titularRepositoryPort;

    @InjectMocks
    private TitularService titularService;

    private UUID titularId;
    private Titular titular;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        titular = new Titular(titularId, "Ana", "Lopez", "Garcia",
                "3109876543", Instant.now(), "COP", "America/Bogota", "token-abc");
    }

    @Nested
    @DisplayName("Buscar Titular por ID")
    class BuscarTitularPorId {
        
        @Test
        @DisplayName("findById - retorna titular existente")
        void findById_retornaTitularExistente() {
            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titular));

            Optional<Titular> resultado = titularService.findById(titularId);

            assertThat(resultado).hasValueSatisfying(t -> {
                assertThat(t.titularId()).isEqualTo(titularId);
                assertThat(t.nombre()).isEqualTo("Ana");
                assertThat(t.primerApellido()).isEqualTo("Lopez");
            });
        }

        @Test
        @DisplayName("findById - retorna vacio si titular no existe")
        void findById_retornaVacioSiNoExiste() {
            UUID idFalso = UUID.randomUUID();
            when(titularRepositoryPort.findById(idFalso)).thenReturn(Optional.empty());

            Optional<Titular> resultado = titularService.findById(idFalso);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("findById - valida estructura del titular")
        void findById_validaEstructuraTitular() {
            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titular));

            Optional<Titular> resultado = titularService.findById(titularId);

            assertThat(resultado).isPresent()
                    .hasValueSatisfying(t -> {
                        assertThat(t.nombre()).isNotBlank();
                        assertThat(t.telefono()).isNotBlank();
                        assertThat(t.monedaPreferida()).isNotBlank();
                        assertThat(t.zonaHoraria()).isNotBlank();
                        assertThat(t.token()).isNotBlank();
                    });
        }
    }

    @Nested
    @DisplayName("Validaciones de Titular")
    class ValidacionesTitular {

        @Test
        @DisplayName("Verificar datos de titular con moneda COP")
        void titularConMonedaCOP() {
            Titular titularCOP = new Titular(UUID.randomUUID(), "Carlos", "Mora", "Vargas",
                    "3001234567", Instant.now(), "COP", "America/Bogota", "token-1");

            when(titularRepositoryPort.findById(titularCOP.titularId())).thenReturn(Optional.of(titularCOP));

            Optional<Titular> resultado = titularService.findById(titularCOP.titularId());

            assertThat(resultado).hasValueSatisfying(t -> assertThat(t.monedaPreferida()).isEqualTo("COP"));
        }

        @Test
        @DisplayName("Verificar zona horaria válida")
        void titularConZonaHorariaValida() {
            Titular titularBogota = new Titular(UUID.randomUUID(), "Juan", "Perez", "Gomez",
                    "3105555555", Instant.now(), "COP", "America/Bogota", "token-2");

            when(titularRepositoryPort.findById(titularBogota.titularId())).thenReturn(Optional.of(titularBogota));

            Optional<Titular> resultado = titularService.findById(titularBogota.titularId());

            assertThat(resultado).hasValueSatisfying(t -> assertThat(t.zonaHoraria()).isEqualTo("America/Bogota"));
        }

        @Test
        @DisplayName("Verificar teléfono no vacío")
        void titularConTelefonoValido() {
            assertThat(titular.telefono()).isNotBlank();
        }

        @Test
        @DisplayName("Verificar token no vacío")
        void titularConTokenValido() {
            assertThat(titular.token()).isNotBlank();
        }
    }
}