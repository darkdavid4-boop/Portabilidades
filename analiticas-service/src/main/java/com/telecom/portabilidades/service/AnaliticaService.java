package com.telecom.portabilidades.service;

import com.telecom.portabilidades.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnaliticaService {

    private static final String CONCEPTO_PORTABILIDADES = "Portabilidades";
    private static final String CONCEPTO_LINEAS = "Líneas";
    private static final String SERVICIO_BANDA_ANCHA_FIJA = "Banda ancha fija minorista";

    private final RegistroClient registroClient;

    public AnaliticaService(RegistroClient registroClient) {
        this.registroClient = registroClient;
    }

    public List<BalancePortabilidadDto> obtenerBalanceNetoPortabilidades(String mes) {
        List<RegistroApiDto> todos = obtenerRegistros();
        validarFechaMes(mes, todos);
        List<RegistroApiDto> registros = todos.stream()
                .filter(r -> CONCEPTO_PORTABILIDADES.equalsIgnoreCase(r.getConcepto())
                        && mes.trim().equals(r.getMes()))
                .toList();
        Map<String, long[]> acumuladoPorOperador = new HashMap<>();

        for (RegistroApiDto registro : registros) {
            String operador = registro.getOperador();
            if (operador == null || operador.equalsIgnoreCase("N/A") || registro.getPortabilidades() == null) {
                continue;
            }
            acumuladoPorOperador.putIfAbsent(operador, new long[]{0L, 0L});
            if ("Importados".equalsIgnoreCase(registro.getTipoPortabilidad())) {
                acumuladoPorOperador.get(operador)[0] += registro.getPortabilidades();
            } else if ("Exportados".equalsIgnoreCase(registro.getTipoPortabilidad())) {
                acumuladoPorOperador.get(operador)[1] += registro.getPortabilidades();
            }
        }

        List<BalancePortabilidadDto> resultado = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : acumuladoPorOperador.entrySet()) {
            long importados = entry.getValue()[0];
            long exportados = entry.getValue()[1];
            resultado.add(new BalancePortabilidadDto(
                    entry.getKey(), mes.trim(), importados, exportados, importados - exportados));
        }
        resultado.sort(Comparator.comparing(BalancePortabilidadDto::getBalanceNeto).reversed()
                .thenComparing(BalancePortabilidadDto::getOperador));
        return resultado;
    }

    public List<RankingPortabilidadDto> obtenerRankingPortabilidades(int año) {
        List<RegistroApiDto> todos = obtenerRegistros();
        validarAño(año, todos);
        List<RegistroApiDto> registros = todos.stream()
                .filter(r -> CONCEPTO_PORTABILIDADES.equalsIgnoreCase(r.getConcepto())
                        && r.getMes() != null && r.getMes().startsWith(String.valueOf(año)))
                .toList();
        Map<String, long[]> acumuladoPorOperador = new HashMap<>();

        for (RegistroApiDto registro : registros) {
            String operador = registro.getOperador();
            if (operador == null || operador.equalsIgnoreCase("N/A") || registro.getPortabilidades() == null) {
                continue;
            }
            acumuladoPorOperador.putIfAbsent(operador, new long[]{0L, 0L});
            if ("Importados".equalsIgnoreCase(registro.getTipoPortabilidad())) {
                acumuladoPorOperador.get(operador)[0] += registro.getPortabilidades();
            } else if ("Exportados".equalsIgnoreCase(registro.getTipoPortabilidad())) {
                acumuladoPorOperador.get(operador)[1] += registro.getPortabilidades();
            }
        }

        List<RankingPortabilidadDto> ranking = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : acumuladoPorOperador.entrySet()) {
            long importados = entry.getValue()[0];
            long exportados = entry.getValue()[1];
            ranking.add(new RankingPortabilidadDto(
                    0, entry.getKey(), String.valueOf(año), importados, exportados, importados - exportados));
        }
        ranking.sort(Comparator.comparing(RankingPortabilidadDto::getTotalImportados).reversed()
                .thenComparing(RankingPortabilidadDto::getOperador));
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setPosicion(i + 1);
        }
        return ranking;
    }

    public List<EvolucionFibraDto> obtenerEvolucionFibra() {
        List<RegistroApiDto> todos = obtenerRegistros();
        Map<String, String> maxMesPorAño = new HashMap<>();
        for (RegistroApiDto registro : todos) {
            String mes = registro.getMes();
            if (mes != null && mes.length() >= 7) {
                String año = mes.substring(0, 4);
                maxMesPorAño.merge(año, mes, (current, candidate) ->
                        candidate.compareTo(current) > 0 ? candidate : current);
            }
        }

        List<String> añosOrdenados = new ArrayList<>(maxMesPorAño.keySet());
        Collections.sort(añosOrdenados);
        List<EvolucionFibraDto> evolucion = new ArrayList<>();
        for (String año : añosOrdenados) {
            String mesCorte = maxMesPorAño.get(año);
            long ftth = 0;
            long xdsl = 0;
            long hfc = 0;

            for (RegistroApiDto registro : todos) {
                if (!mesCorte.equals(registro.getMes())
                        || !SERVICIO_BANDA_ANCHA_FIJA.equalsIgnoreCase(registro.getServicio())
                        || !CONCEPTO_LINEAS.equalsIgnoreCase(registro.getConcepto())
                        || registro.getLineas() == null) {
                    continue;
                }
                String tecnologia = registro.getTecnologiaAcceso();
                if ("FTTH".equalsIgnoreCase(tecnologia)) {
                    ftth += registro.getLineas();
                } else if ("xDSL".equalsIgnoreCase(tecnologia)) {
                    xdsl += registro.getLineas();
                } else if ("HFC".equalsIgnoreCase(tecnologia)) {
                    hfc += registro.getLineas();
                }
            }

            long total = ftth + xdsl + hfc;
            double porcentaje = total > 0
                    ? BigDecimal.valueOf((double) ftth * 100.0 / total)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;
            evolucion.add(new EvolucionFibraDto(
                    año + " (" + mesCorte + ")", ftth, xdsl, hfc, total, porcentaje));
        }
        return evolucion;
    }

    public List<CuotaMercadoDto> obtenerCuotaMercadoBandaAncha(String mes) {
        List<RegistroApiDto> todos = obtenerRegistros();
        validarFechaMes(mes, todos);
        Map<String, Long> lineasPorOperador = new HashMap<>();
        long totalLineas = 0;

        for (RegistroApiDto registro : todos) {
            if (!mes.trim().equals(registro.getMes())
                    || !SERVICIO_BANDA_ANCHA_FIJA.equalsIgnoreCase(registro.getServicio())
                    || !CONCEPTO_LINEAS.equalsIgnoreCase(registro.getConcepto())
                    || registro.getLineas() == null) {
                continue;
            }
            String operador = registro.getOperador();
            if (operador == null || operador.equalsIgnoreCase("N/A")) {
                continue;
            }
            lineasPorOperador.merge(operador, registro.getLineas(), Long::sum);
            totalLineas += registro.getLineas();
        }

        List<CuotaMercadoDto> cuotas = new ArrayList<>();
        for (Map.Entry<String, Long> entry : lineasPorOperador.entrySet()) {
            double porcentaje = totalLineas > 0
                    ? BigDecimal.valueOf((double) entry.getValue() * 100.0 / totalLineas)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;
            cuotas.add(new CuotaMercadoDto(entry.getKey(), mes.trim(), entry.getValue(), porcentaje));
        }
        cuotas.sort(Comparator.comparing(CuotaMercadoDto::getLineasBandaAncha).reversed()
                .thenComparing(CuotaMercadoDto::getOperador));
        return cuotas;
    }

    public ResumenAnualDto obtenerResumenAnual(int año) {
        List<RegistroApiDto> todos = obtenerRegistros();
        validarAño(año, todos);
        String añoTexto = String.valueOf(año);
        List<RegistroApiDto> registrosAño = todos.stream()
                .filter(r -> r.getMes() != null && r.getMes().startsWith(añoTexto))
                .toList();
        long totalPortabilidades = registrosAño.stream()
                .filter(r -> CONCEPTO_PORTABILIDADES.equalsIgnoreCase(r.getConcepto())
                        && "Importados".equalsIgnoreCase(r.getTipoPortabilidad())
                        && r.getPortabilidades() != null)
                .mapToLong(RegistroApiDto::getPortabilidades)
                .sum();

        String mesCorte = registrosAño.stream()
                .map(RegistroApiDto::getMes)
                .filter(Objects::nonNull)
                .max(String::compareTo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No hay registros para el año solicitado."));

        long totalFijas = 0;
        long totalMoviles = 0;
        long totalFibra = 0;
        for (RegistroApiDto registro : registrosAño) {
            if (!mesCorte.equals(registro.getMes())
                    || !CONCEPTO_LINEAS.equalsIgnoreCase(registro.getConcepto())
                    || registro.getLineas() == null) {
                continue;
            }
            String servicio = registro.getServicio() == null ? "" : registro.getServicio();
            if ("Telefonía fija".equalsIgnoreCase(servicio)) {
                totalFijas += registro.getLineas();
            } else if ("Telefonía móvil".equalsIgnoreCase(servicio)) {
                totalMoviles += registro.getLineas();
            }
            if (SERVICIO_BANDA_ANCHA_FIJA.equalsIgnoreCase(servicio)
                    && "FTTH".equalsIgnoreCase(registro.getTecnologiaAcceso())) {
                totalFibra += registro.getLineas();
            }
        }

        return new ResumenAnualDto(año, totalFijas, totalMoviles, totalFibra, totalPortabilidades);
    }

    public List<String> obtenerAñosDisponibles() {
        return obtenerMesesDisponibles().stream()
                .map(mes -> mes.substring(0, 4))
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public List<String> obtenerMesesDisponibles() {
        return obtenerRegistros().stream()
                .map(RegistroApiDto::getMes)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    private List<RegistroApiDto> obtenerRegistros() {
        List<RegistroApiDto> registros = registroClient.obtenerTodos();
        if (registros.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "No hay datos cargados para realizar la consulta.");
        }
        return registros;
    }

    private void validarFechaMes(String mes, List<RegistroApiDto> registros) {
        if (mes == null || mes.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro mes no puede estar vacío.");
        }
        YearMonth solicitada;
        try {
            solicitada = YearMonth.parse(mes.trim());
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El mes debe tener el formato AAAA-MM, por ejemplo 2024-05.");
        }
        YearMonth minimo = YearMonth.parse(registros.stream()
                .map(RegistroApiDto::getMes).filter(Objects::nonNull).min(String::compareTo).orElseThrow());
        YearMonth maximo = YearMonth.parse(registros.stream()
                .map(RegistroApiDto::getMes).filter(Objects::nonNull).max(String::compareTo).orElseThrow());
        if (solicitada.isBefore(minimo) || solicitada.isAfter(maximo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El mes debe estar entre " + minimo + " y " + maximo + ".");
        }
    }

    private void validarAño(int año, List<RegistroApiDto> registros) {
        int minimo = registros.stream().map(RegistroApiDto::getMes).filter(Objects::nonNull)
                .mapToInt(mes -> Integer.parseInt(mes.substring(0, 4))).min().orElseThrow();
        int maximo = registros.stream().map(RegistroApiDto::getMes).filter(Objects::nonNull)
                .mapToInt(mes -> Integer.parseInt(mes.substring(0, 4))).max().orElseThrow();
        if (año < minimo || año > maximo) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El año debe estar entre " + minimo + " y " + maximo + ".");
        }
    }
}
