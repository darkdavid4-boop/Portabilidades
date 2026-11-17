package com.telecom.portabilidades.service;

import com.telecom.portabilidades.model.RegistroTelecomunicacion;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Types;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;

@Component
public class CsvDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvDataLoader.class);
    private static final List<String> HEADERS = List.of(
            "Mes", "País", "Servicio", "Concepto", "Operador", "Segmento",
            "Tecnología de Acceso", "Contrato", "Terminal de Telefonía Móvil",
            "Línea BA Móvil", "Tipo de BA Mayorista", "Tipo de Portabilidad",
            "Unidades", "Líneas (Núm)", "Tasa de Penetración (%)", "Portabilidades (Núm)"
    );

    private final JdbcTemplate jdbcTemplate;
    private final String dataPath;

    public CsvDataLoader(JdbcTemplate jdbcTemplate, @Value("${app.data.path:}") String dataPath) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataPath = dataPath;
    }

    @Override
    @Transactional
    public void run(String... args) throws IOException {
        long startTime = System.currentTimeMillis();
        byte[] source = readDataset();
        String checksum = sha256(source);
        List<String> importedChecksums = jdbcTemplate.queryForList(
                "SELECT checksum FROM dataset_import WHERE id = 1", String.class);
        if (importedChecksums.contains(checksum)) {
            log.info("El CSV ya está cargado ({} filas); no se repite la importación.", rowCount());
            return;
        }

        List<RegistroTelecomunicacion> records = parseCsv(source);
        jdbcTemplate.update("DELETE FROM registros_telecomunicaciones");
        jdbcTemplate.update("DELETE FROM dataset_import");
        jdbcTemplate.batchUpdate(
                "INSERT INTO registros_telecomunicaciones (" +
                        "mes, pais, servicio, concepto, operador, segmento, tecnologia_acceso, contrato, " +
                        "terminal_telefonia_movil, linea_ba_movil, tipo_ba_mayorista, tipo_portabilidad, " +
                        "unidades, lineas, tasa_penetracion, portabilidades) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                records,
                1000,
                (statement, record) -> {
                    statement.setString(1, record.getMes());
                    statement.setString(2, record.getPais());
                    statement.setString(3, record.getServicio());
                    statement.setString(4, record.getConcepto());
                    statement.setString(5, record.getOperador());
                    statement.setString(6, record.getSegmento());
                    statement.setString(7, record.getTecnologiaAcceso());
                    statement.setString(8, record.getContrato());
                    statement.setString(9, record.getTerminalTelefoniaMovil());
                    statement.setString(10, record.getLineaBaMovil());
                    statement.setString(11, record.getTipoBaMayorista());
                    statement.setString(12, record.getTipoPortabilidad());
                    statement.setString(13, record.getUnidades());
                    setLongOrNull(statement, 14, record.getLineas());
                    if (record.getTasaPenetracion() == null) {
                        statement.setNull(15, Types.DOUBLE);
                    } else {
                        statement.setDouble(15, record.getTasaPenetracion());
                    }
                    setLongOrNull(statement, 16, record.getPortabilidades());
                });
        jdbcTemplate.update(
                "INSERT INTO dataset_import (id, checksum, row_count) VALUES (1, ?, ?)",
                checksum,
                records.size());

        log.info("Importadas {} filas desde el CSV en {} ms.",
                records.size(), System.currentTimeMillis() - startTime);
    }

    List<RegistroTelecomunicacion> parseCsv(byte[] content) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .get();
        List<RegistroTelecomunicacion> records = new ArrayList<>();

        try (CSVParser parser = CSVParser.parse(
                new InputStreamReader(new ByteArrayInputStream(content), StandardCharsets.UTF_8), format)) {
            Iterator<CSVRecord> iterator = parser.iterator();
            if (!iterator.hasNext()) {
                throw new IllegalStateException("El CSV está vacío y no contiene cabecera.");
            }

            CSVRecord header = iterator.next();
            List<String> actualHeaders = new ArrayList<>();
            for (int i = 0; i < header.size(); i++) {
                String value = header.get(i);
                actualHeaders.add(i == 0 ? value.replaceFirst("^\\uFEFF", "") : value);
            }
            if (!HEADERS.equals(actualHeaders)) {
                throw new IllegalStateException("La cabecera del CSV no coincide con el formato esperado.");
            }

            while (iterator.hasNext()) {
                CSVRecord row = iterator.next();
                long rowNumber = row.getRecordNumber();
                if (row.size() != HEADERS.size()) {
                    throw invalidRow(rowNumber, "se esperaban 16 columnas y se encontraron " + row.size());
                }

                String month = parseString(row.get(0));
                try {
                    YearMonth.parse(month);
                } catch (DateTimeParseException | NullPointerException exception) {
                    throw invalidRow(rowNumber, "mes inválido: " + month);
                }

                records.add(new RegistroTelecomunicacion(
                        month,
                        parseString(row.get(1)),
                        parseString(row.get(2)),
                        parseString(row.get(3)),
                        parseString(row.get(4)),
                        parseString(row.get(5)),
                        parseString(row.get(6)),
                        parseString(row.get(7)),
                        parseString(row.get(8)),
                        parseString(row.get(9)),
                        parseString(row.get(10)),
                        parseString(row.get(11)),
                        parseString(row.get(12)),
                        parseLong(row.get(13), rowNumber, HEADERS.get(13)),
                        parseDouble(row.get(14), rowNumber, HEADERS.get(14)),
                        parseLong(row.get(15), rowNumber, HEADERS.get(15))
                ));
            }
        }

        if (records.isEmpty()) {
            throw new IllegalStateException("El CSV contiene la cabecera, pero no contiene registros.");
        }
        return records;
    }

    private byte[] readDataset() throws IOException {
        if (dataPath != null && !dataPath.isBlank()) {
            return Files.readAllBytes(Path.of(dataPath));
        }
        try (InputStream input = new ClassPathResource("ds_24475_1.csv").getInputStream()) {
            return input.readAllBytes();
        }
    }

    private long rowCount() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT row_count FROM dataset_import WHERE id = 1", Long.class);
        return count == null ? 0 : count;
    }

    private String sha256(byte[] content) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible en esta JVM.", exception);
        }
    }

    private IllegalStateException invalidRow(long rowNumber, String detail) {
        return new IllegalStateException("Fila CSV " + rowNumber + ": " + detail);
    }

    private String parseString(String value) {
        String trimmed = value == null ? null : value.trim();
        return trimmed == null || trimmed.isEmpty() ? null : trimmed;
    }

    private Long parseLong(String value, long rowNumber, String column) {
        String parsed = parseString(value);
        if (parsed == null) {
            return null;
        }
        try {
            return Long.parseLong(parsed.replace(".", "").replace(" ", ""));
        } catch (NumberFormatException exception) {
            throw invalidRow(rowNumber, "valor entero inválido en '" + column + "': " + parsed);
        }
    }

    private Double parseDouble(String value, long rowNumber, String column) {
        String parsed = parseString(value);
        if (parsed == null) {
            return null;
        }
        try {
            return Double.parseDouble(parsed.replace(",", ".").replace(" ", ""));
        } catch (NumberFormatException exception) {
            throw invalidRow(rowNumber, "valor decimal inválido en '" + column + "': " + parsed);
        }
    }

    private void setLongOrNull(java.sql.PreparedStatement statement, int index, Long value)
            throws java.sql.SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }
}
