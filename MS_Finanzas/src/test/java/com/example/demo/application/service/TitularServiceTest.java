package com.example.demo.application.service;

import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.domain.model.Titular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

    @Test
    @DisplayName("findById - retorna titular existente")
    void findById_retornaTitularExistente() {
        when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titular));

        Optional<Titular> resultado = titularService.findById(titularId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().titularId()).isEqualTo(titularId);
        assertThat(resultado.get().nombre()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("findById - retorna vacio si titular no existe")
    void findById_retornaVacioSiNoExiste() {
        UUID idFalso = UUID.randomUUID();
        when(titularRepositoryPort.findById(idFalso)).thenReturn(Optional.empty());

        Optional<Titular> resultado = titularService.findById(idFalso);

        assertThat(resultado).isEmpty();
    }
}