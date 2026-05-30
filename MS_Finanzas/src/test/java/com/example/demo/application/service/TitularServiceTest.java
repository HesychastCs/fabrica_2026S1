package com.example.demo.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Titular;

@ExtendWith(MockitoExtension.class)
class TitularServiceTest {

    @Mock
    private TitularRepositoryPort titularRepositoryPort;

    @InjectMocks
    private TitularService titularService;

    private UUID titularId;
    private Titular titular;
    private Titular titularSinId;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        titular = new Titular(
            titularId,
            "Ana",
            "Pérez",
            "Gómez",
            "3001234567",
            Instant.now(),
            "COP",
            "America/Bogota",
            "token-existente"
        );
        titularSinId = new Titular(
            null,
            "Ana",
            "Pérez",
            "Gómez",
            "3001234567",
            null,
            "COP",
            "America/Bogota",
            null
        );
    }

    @Test
    void crearTitularExitoso() {
        given(titularRepositoryPort.save(any(Titular.class))).willAnswer(inv -> {
            Titular t = inv.getArgument(0);
            return new Titular(
                titularId,
                t.nombre(),
                t.primerApellido(),
                t.segundoApellido(),
                t.telefono(),
                t.fechaRegistro(),
                t.monedaPreferida(),
                t.zonaHoraria(),
                t.token()
            );
        });

        Titular created = titularService.createTitular(titularSinId);

        assertEquals("Ana", created.nombre());
        assertNotNull(created.token());
        verify(titularRepositoryPort).save(any(Titular.class));
    }

    @Test
    void actualizarTitularExitoso() {
        Titular updatePayload = new Titular(
            null,
            "Ana María",
            "Pérez",
            "Gómez",
            "3009998877",
            null,
            "USD",
            "America/New_York",
            null
        );
        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.of(titular));
        given(titularRepositoryPort.update(eq(titularId), any(Titular.class))).willAnswer(inv -> inv.getArgument(1));

        Titular updated = titularService.updateTitular(titularId, updatePayload);

        assertEquals("Ana María", updated.nombre());
        assertEquals("USD", updated.monedaPreferida());
        verify(titularRepositoryPort).update(eq(titularId), any(Titular.class));
    }

    @Test
    void eliminarTitularExitoso() {
        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.of(titular));

        titularService.deleteTitular(titularId);

        verify(titularRepositoryPort).deleteById(titularId);
    }

    @Test
    void eliminarTitular_shouldThrowWhenNotFound() {
        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> titularService.deleteTitular(titularId));
    }
}
