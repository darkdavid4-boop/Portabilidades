package com.telecom.portabilidades.service;

import com.telecom.portabilidades.model.RegistroTelecomunicacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class CsvDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvDataLoader.class);
    private final JdbcTemplate jdbcTemplate;

    public CsvDataLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando la carga de datos desde ds_24475_1.csv...");
        long startTime = System.currentTimeMillis();

        InputStream is = null;
        try {
            ClassPathResource resource = new ClassPathResource("ds_24475_1.csv");
            if (resource.exists()) {
                is = resource.getInputStream();
            }
        } catch (Exception ignored) {
        }

        if (is == null) {
            File localFile = new File("ds_24475_1.csv");
            if (localFile.exists()) {
                is = new FileInputStream(localFile);
            } else {
                File parentFile = new File("c:/Users/PC-14u/Downloads/ds_24475_1.csv");
                if (parentFile.exists()) {
                    is = new FileInputStream(parentFile);
                }
            }
        }

        if (is == null) {
            log.error("No se encontró el archivo ds_24475_1.csv en el classpath ni en el disco local.");
            return;
        }

        List<RegistroTelecomunicacion> registros = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // Cabecera
            if (line == null) {
                log.warn("El archivo CSV está vacío.");
                return;
            }

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] cols = line.split(";", -1);
                if (cols.length < 16) {
                    continue;
                }

                RegistroTelecomunicacion reg = new RegistroTelecomunicacion(
                        parseString(cols[0]),  // Mes
                        parseString(cols[1]),  // País
                        parseString(cols[2]),  // Servicio
                        parseString(cols[3]),  // Concepto
                        parseString(cols[4]),  // Operador
                        parseString(cols[5]),  // Segmento
                        parseString(cols[6]),  // Tecnología de Acceso
                        parseString(cols[7]),  // Contrato
                        parseString(cols[8]),  // Terminal Telefonía Móvil
                        parseString(cols[9]),  // Línea BA Móvil
                        parseString(cols[10]), // Tipo BA Mayorista
                        parseString(cols[11]), // Tipo Portabilidad
                        parseString(cols[12]), // Unidades
                        parseLong(cols[13]),   // Líneas (Núm)
                        parseDouble(cols[14]), // Tasa Penetración (%)
                        parseLong(cols[15])    // Portabilidades (Núm)
                );
                registros.add(reg);
            }
        }

        log.info("Total registros leídos del CSV: {}. Insertando en base de datos H2...", registros.size());

        String sql = "INSERT INTO registros_telecomunicaciones (" +
                "mes, pais, servicio, concepto, operador, segmento, tecnologia_acceso, contrato, " +
                "terminal_telefonia_movil, linea_ba_movil, tipo_ba_mayorista, tipo_portabilidad, " +
                "unidades, lineas, tasa_penetracion, portabilidades) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int batchSize = 1000;
        for (int i = 0; i < registros.size(); i += batchSize) {
            List<RegistroTelecomunicacion> batch = registros.subList(i, Math.min(i + batchSize, registros.size()));
            jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, reg) -> {
                ps.setString(1, reg.getMes());
                ps.setString(2, reg.getPais());
                ps.setString(3, reg.getServicio());
                ps.setString(4, reg.getConcepto());
                ps.setString(5, reg.getOperador());
                ps.setString(6, reg.getSegmento());
                ps.setString(7, reg.getTecnologiaAcceso());
                ps.setString(8, reg.getContrato());
                ps.setString(9, reg.getTerminalTelefoniaMovil());
                ps.setString(10, reg.getLineaBaMovil());
                ps.setString(11, reg.getTipoBaMayorista());
                ps.setString(12, reg.getTipoPortabilidad());
                ps.setString(13, reg.getUnidades());

                if (reg.getLineas() != null) {
                    ps.setLong(14, reg.getLineas());
                } else {
                    ps.setNull(14, Types.BIGINT);
                }

                if (reg.getTasaPenetracion() != null) {
                    ps.setDouble(15, reg.getTasaPenetracion());
                } else {
                    ps.setNull(15, Types.DOUBLE);
                }

                if (reg.getPortabilidades() != null) {
                    ps.setLong(16, reg.getPortabilidades());
                } else {
                    ps.setNull(16, Types.BIGINT);
                }
            });
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Carga de datos completada exitosamente en {} ms. Total registros cargados: {}", duration, registros.size());
    }

    private String parseString(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Long parseLong(String value) {
        String str = parseString(value);
        if (str == null) {
            return null;
        }
        try {
            return Long.parseLong(str.replace(".", "").replace(" ", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        String str = parseString(value);
        if (str == null) {
            return null;
        }
        try {
            return Double.parseDouble(str.replace(",", ".").replace(" ", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
