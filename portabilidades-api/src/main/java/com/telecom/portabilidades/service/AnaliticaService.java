package com.telecom.portabilidades.service;

import com.telecom.portabilidades.dto.*;
import com.telecom.portabilidades.model.RegistroTelecomunicacion;
import com.telecom.portabilidades.repository.RegistroRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnaliticaService {

    private final RegistroRepository registroRepository;
    private final RegistroService registroService;

    public AnaliticaService(RegistroRepository registroRepository, RegistroService registroService) {
        this.registroRepository = registroRepository;
        this.registroService = registroService;
    }

    public List<BalancePortabilidadDto> obtenerBalanceNetoPortabilidades(String mes) {
        registroService.validarBaseDatosNoVacia();
        registroService.validarFechaMes(mes);

        List<RegistroTelecomunicacion> registros = registroRepository.findByConceptoAndMes("Portabilidades", mes.trim());
        Map<String, long[]> acumuladoPorOperador = new HashMap<>();

        for (RegistroTelecomunicacion r : registros) {
            String op = r.getOperador();
            if (op == null || op.equalsIgnoreCase("N/A") || r.getPortabilidades() == null) {
                continue;
            }
            acumuladoPorOperador.putIfAbsent(op, new long[]{0L, 0L}); // [importados, exportados]
            if ("Importados".equalsIgnoreCase(r.getTipoPortabilidad())) {
                acumuladoPorOperador.get(op)[0] += r.getPortabilidades();
            } else if ("Exportados".equalsIgnoreCase(r.getTipoPortabilidad())) {
                acumuladoPorOperador.get(op)[1] += r.getPortabilidades();
            }
        }

        List<BalancePortabilidadDto> resultado = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : acumuladoPorOperador.entrySet()) {
            long imp = entry.getValue()[0];
            long exp = entry.getValue()[1];
            resultado.add(new BalancePortabilidadDto(entry.getKey(), mes, imp, exp, imp - exp));
        }

        resultado.sort(Comparator.comparing(BalancePortabilidadDto::getBalanceNeto).reversed());
        return resultado;
    }

    public List<RankingPortabilidadDto> obtenerRankingPortabilidades(int año) {
        registroService.validarBaseDatosNoVacia();
        registroService.validarAño(año);

        List<RegistroTelecomunicacion> registros = registroRepository.findByConceptoAndMesStartingWith("Portabilidades", String.valueOf(año));
        Map<String, long[]> acumuladoPorOperador = new HashMap<>();

        for (RegistroTelecomunicacion r : registros) {
            String op = r.getOperador();
            if (op == null || op.equalsIgnoreCase("N/A") || r.getPortabilidades() == null) {
                continue;
            }
            acumuladoPorOperador.putIfAbsent(op, new long[]{0L, 0L});
            if ("Importados".equalsIgnoreCase(r.getTipoPortabilidad())) {
                acumuladoPorOperador.get(op)[0] += r.getPortabilidades();
            } else if ("Exportados".equalsIgnoreCase(r.getTipoPortabilidad())) {
                acumuladoPorOperador.get(op)[1] += r.getPortabilidades();
            }
        }

        List<RankingPortabilidadDto> ranking = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : acumuladoPorOperador.entrySet()) {
            long imp = entry.getValue()[0];
            long exp = entry.getValue()[1];
            ranking.add(new RankingPortabilidadDto(0, entry.getKey(), String.valueOf(año), imp, exp, imp - exp));
        }

        ranking.sort(Comparator.comparing(RankingPortabilidadDto::getTotalImportados).reversed());
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setPosicion(i + 1);
        }

        return ranking;
    }

    public List<EvolucionFibraDto> obtenerEvolucionFibra() {
        registroService.validarBaseDatosNoVacia();

        List<RegistroTelecomunicacion> todos = registroRepository.findAll();
        Map<String, String> maxMesPorAño = new HashMap<>();
        for (RegistroTelecomunicacion r : todos) {
            String mes = r.getMes();
            if (mes != null && mes.length() >= 7) {
                String anio = mes.substring(0, 4);
                if (!maxMesPorAño.containsKey(anio) || mes.compareTo(maxMesPorAño.get(anio)) > 0) {
                    maxMesPorAño.put(anio, mes);
                }
            }
        }

        List<String> añosOrdenados = new ArrayList<>(maxMesPorAño.keySet());
        Collections.sort(añosOrdenados);

        List<EvolucionFibraDto> evolucion = new ArrayList<>();
        for (String anio : añosOrdenados) {
            String mesCorte = maxMesPorAño.get(anio);
            List<RegistroTelecomunicacion> registrosMes = registroRepository.findByServicioAndMes("Banda ancha fija minorista", mesCorte);

            long ftth = 0;
            long xdsl = 0;
            long hfc = 0;

            for (RegistroTelecomunicacion r : registrosMes) {
                if (r.getLineas() == null || !"Líneas".equalsIgnoreCase(r.getConcepto())) {
                    continue;
                }
                String tec = r.getTecnologiaAcceso();
                if ("FTTH".equalsIgnoreCase(tec)) {
                    ftth += r.getLineas();
                } else if ("xDSL".equalsIgnoreCase(tec)) {
                    xdsl += r.getLineas();
                } else if ("HFC".equalsIgnoreCase(tec)) {
                    hfc += r.getLineas();
                }
            }

            long total = ftth + xdsl + hfc;
            double pct = total > 0 ? BigDecimal.valueOf((double) ftth * 100.0 / total).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0;
            evolucion.add(new EvolucionFibraDto(anio + " (" + mesCorte + ")", ftth, xdsl, hfc, total, pct));
        }

        return evolucion;
    }

    public List<CuotaMercadoDto> obtenerCuotaMercadoBandaAncha(String mes) {
        registroService.validarBaseDatosNoVacia();
        registroService.validarFechaMes(mes);

        List<RegistroTelecomunicacion> registros = registroRepository.findByServicioAndMes("Banda ancha fija minorista", mes.trim());
        Map<String, Long> lineasPorOperador = new HashMap<>();
        long totalLineas = 0;

        for (RegistroTelecomunicacion r : registros) {
            if (!"Líneas".equalsIgnoreCase(r.getConcepto()) || r.getLineas() == null) {
                continue;
            }
            String op = r.getOperador();
            if (op == null || op.equalsIgnoreCase("N/A")) {
                continue;
            }
            lineasPorOperador.put(op, lineasPorOperador.getOrDefault(op, 0L) + r.getLineas());
            totalLineas += r.getLineas();
        }

        List<CuotaMercadoDto> cuotas = new ArrayList<>();
        for (Map.Entry<String, Long> entry : lineasPorOperador.entrySet()) {
            double pct = totalLineas > 0 ? BigDecimal.valueOf((double) entry.getValue() * 100.0 / totalLineas).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0;
            cuotas.add(new CuotaMercadoDto(entry.getKey(), mes, entry.getValue(), pct));
        }

        cuotas.sort(Comparator.comparing(CuotaMercadoDto::getLineasBandaAncha).reversed());
        return cuotas;
    }

    public ResumenAnualDto obtenerResumenAnual(int año) {
        registroService.validarBaseDatosNoVacia();
        registroService.validarAño(año);

        String añoStr = String.valueOf(año);
        List<RegistroTelecomunicacion> registrosAño = registroRepository.findByMesStartingWith(añoStr);

        long totalPortabilidades = 0;
        String mesCorte = añoStr + "-01";
        for (RegistroTelecomunicacion r : registrosAño) {
            if ("Portabilidades".equalsIgnoreCase(r.getConcepto()) && r.getPortabilidades() != null) {
                totalPortabilidades += r.getPortabilidades();
            }
            if (r.getMes() != null && r.getMes().compareTo(mesCorte) > 0) {
                mesCorte = r.getMes();
            }
        }

        long totalFijas = 0;
        long totalMoviles = 0;
        long totalFibra = 0;

        for (RegistroTelecomunicacion r : registrosAño) {
            if (!mesCorte.equals(r.getMes()) || !"Líneas".equalsIgnoreCase(r.getConcepto()) || r.getLineas() == null) {
                continue;
            }
            String serv = r.getServicio() != null ? r.getServicio() : "";
            if (serv.equalsIgnoreCase("Telefonía fija")) {
                totalFijas += r.getLineas();
            } else if (serv.equalsIgnoreCase("Telefonía móvil")) {
                totalMoviles += r.getLineas();
            }
            if ("FTTH".equalsIgnoreCase(r.getTecnologiaAcceso())) {
                totalFibra += r.getLineas();
            }
        }

        return new ResumenAnualDto(año, totalFijas, totalMoviles, totalFibra, totalPortabilidades);
    }

    public List<String> obtenerAñosDisponibles() {
        registroService.validarBaseDatosNoVacia();
        List<String> meses = registroRepository.findDistinctMeses();
        return meses.stream()
                .filter(m -> m != null && m.length() >= 4)
                .map(m -> m.substring(0, 4))
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }
}
