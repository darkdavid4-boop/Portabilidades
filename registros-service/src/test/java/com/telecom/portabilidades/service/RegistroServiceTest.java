package com.telecom.portabilidades.service;

import com.telecom.portabilidades.exception.RangoFechaInvalidoException;
import com.telecom.portabilidades.repository.RegistroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RegistroServiceTest {

    @Mock
    private RegistroRepository registroRepository;

    private RegistroService registroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registroService = new RegistroService(registroRepository);
        when(registroRepository.count()).thenReturn(1L);
        when(registroRepository.findMesMinimo()).thenReturn("2007-01");
        when(registroRepository.findMesMaximo()).thenReturn("2026-08");
    }

    @Test
    void rejectsMonthsOutsideImportedDataRange() {
        assertThrows(RangoFechaInvalidoException.class,
                () -> registroService.validarFechaMes("2026-09"));
    }

    @Test
    void capsRequestedPageSize() {
        assertThrows(IllegalArgumentException.class,
                () -> registroService.obtenerTodos(0, 501));
    }
}
