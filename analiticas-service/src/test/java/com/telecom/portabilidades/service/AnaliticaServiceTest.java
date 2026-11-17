package com.telecom.portabilidades.service;

import com.telecom.portabilidades.dto.RegistroApiDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AnaliticaServiceTest {

    @Mock
    private RegistroClient registroClient;

    private AnaliticaService analiticaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        analiticaService = new AnaliticaService(registroClient);
        when(registroClient.obtenerTodos()).thenReturn(List.of(
                registro("2024-12", "Telefonía fija", "Líneas", "Movistar", "N/A", "N/A", 300L, null),
                registro("2024-12", "Telefonía móvil", "Líneas", "Movistar", "N/A", "N/A", 200L, null),
                registro("2024-12", "Banda ancha fija minorista", "Líneas", "Movistar", "FTTH", "N/A", 100L, null),
                registro("2024-12", "Banda ancha fija minorista", "Líneas", "DIGI", "FTTH", "N/A", 50L, null),
                registro("2024-12", "Telefonía móvil", "Portabilidades", "Movistar", "N/A", "Importados", null, 12L),
                registro("2024-12", "Telefonía móvil", "Portabilidades", "Movistar", "N/A", "Exportados", null, 20L),
                registro("2024-12", "Telefonía móvil", "Portabilidades", "DIGI", "N/A", "Importados", null, 5L)
        ));
    }

    @Test
    void annualSummaryCountsEachPortabilityOnceAndTotalsFiberOnlyFromFixedBroadband() {
        var summary = analiticaService.obtenerResumenAnual(2024);

        assertEquals(300L, summary.getTotalLineasFijas());
        assertEquals(200L, summary.getTotalLineasMoviles());
        assertEquals(150L, summary.getTotalLineasFibra());
        assertEquals(17L, summary.getTotalPortabilidades());
    }

    @Test
    void monthlyBalanceUsesImportsAndExportsPerOperator() {
        var balance = analiticaService.obtenerBalanceNetoPortabilidades("2024-12");

        assertEquals("DIGI", balance.getFirst().getOperador());
        assertEquals(5L, balance.getFirst().getBalanceNeto());
        assertEquals(-8L, balance.get(1).getBalanceNeto());
    }

    @Test
    void rejectsYearsOutsideAvailableData() {
        assertThrows(ResponseStatusException.class,
                () -> analiticaService.obtenerResumenAnual(2023));
    }

    @Test
    void availableMonthsAreDistinctAndNewestFirst() {
        assertEquals(List.of("2024-12"), analiticaService.obtenerMesesDisponibles());
    }

    private RegistroApiDto registro(
            String mes, String servicio, String concepto, String operador,
            String tecnologia, String tipoPortabilidad, Long lineas, Long portabilidades) {
        return new RegistroApiDto(
                null, mes, "España", servicio, concepto, operador, "N/A",
                tecnologia, "N/A", "N/A", "N/A", "N/A", tipoPortabilidad,
                "Unidades", lineas, null, portabilidades);
    }
}
