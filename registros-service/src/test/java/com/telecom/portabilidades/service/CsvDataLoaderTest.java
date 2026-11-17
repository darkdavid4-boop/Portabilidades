package com.telecom.portabilidades.service;

import com.telecom.portabilidades.model.RegistroTelecomunicacion;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CsvDataLoaderTest {

    private static final String HEADER = String.join(";",
            "Mes", "País", "Servicio", "Concepto", "Operador", "Segmento",
            "Tecnología de Acceso", "Contrato", "Terminal de Telefonía Móvil",
            "Línea BA Móvil", "Tipo de BA Mayorista", "Tipo de Portabilidad",
            "Unidades", "Líneas (Núm)", "Tasa de Penetración (%)", "Portabilidades (Núm)");

    private final CsvDataLoader loader = new CsvDataLoader(mock(JdbcTemplate.class), "");

    @Test
    void parsesDelimitedRowsAndSpanishDecimals() throws Exception {
        String csv = HEADER + "\n"
                + "2024-01;España;Telefonía móvil;Portabilidades;Movistar;N/A;N/A;N/A;N/A;N/A;N/A;"
                + "Importados;Unidades;;2,5;12\n";

        List<RegistroTelecomunicacion> records = loader.parseCsv(csv.getBytes(StandardCharsets.UTF_8));

        assertEquals(1, records.size());
        assertNull(records.getFirst().getLineas());
        assertEquals(2.5, records.getFirst().getTasaPenetracion());
        assertEquals(12L, records.getFirst().getPortabilidades());
    }

    @Test
    void rejectsMalformedNumericValuesInsteadOfDroppingThem() {
        String csv = HEADER + "\n"
                + "2024-01;España;Telefonía móvil;Portabilidades;Movistar;N/A;N/A;N/A;N/A;N/A;N/A;"
                + "Importados;Unidades;;2,5;12x\n";

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> loader.parseCsv(csv.getBytes(StandardCharsets.UTF_8)));

        assertTrue(exception.getMessage().contains("Fila CSV 2"));
    }

    @Test
    void parsesTheBundledDatasetWithoutDroppingRows() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/ds_24475_1.csv")) {
            List<RegistroTelecomunicacion> records = loader.parseCsv(input.readAllBytes());

            assertEquals(15113, records.size());
            assertEquals("2007-01", records.getFirst().getMes());
            assertEquals("2026-08", records.getLast().getMes());
        }
    }
}
