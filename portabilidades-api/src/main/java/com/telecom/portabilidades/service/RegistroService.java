package com.telecom.portabilidades.service;

import com.telecom.portabilidades.dto.PaginatedResponse;
import com.telecom.portabilidades.dto.RegistroDto;
import com.telecom.portabilidades.exception.BaseDatosVaciaException;
import com.telecom.portabilidades.exception.OperadorNoEncontradoException;
import com.telecom.portabilidades.exception.RangoFechaInvalidoException;
import com.telecom.portabilidades.exception.SinRegistrosParaOperadorException;
import com.telecom.portabilidades.model.RegistroTelecomunicacion;
import com.telecom.portabilidades.repository.RegistroRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistroService {

    private static final YearMonth FECHA_MINIMA = YearMonth.of(2007, 1);
    private final RegistroRepository registroRepository;

    public RegistroService(RegistroRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    public void validarBaseDatosNoVacia() {
        if (registroRepository.count() == 0) {
            throw new BaseDatosVaciaException("La base de datos está vacía. No se han cargado registros de telecomunicaciones.");
        }
    }

    public void validarFechaMes(String mes) {
        if (mes == null || mes.trim().isEmpty()) {
            throw new IllegalArgumentException("El parámetro de fecha no puede estar vacío.");
        }
        YearMonth ym;
        try {
            ym = YearMonth.parse(mes.trim());
        } catch (DateTimeParseException e) {
            throw new RangoFechaInvalidoException("Formato de fecha inválido ('" + mes + "'). Debe usar el formato AAAA-MM (ejemplo: 2024-05).");
        }

        YearMonth fechaMaxima = YearMonth.of(2026, 12);
        if (YearMonth.now().isAfter(fechaMaxima)) {
            fechaMaxima = YearMonth.now();
        }
        if (ym.isBefore(FECHA_MINIMA)) {
            throw new RangoFechaInvalidoException(String.format(
                    "La fecha solicitada ('%s') es anterior a enero de 2007 (2007-01), fecha en la que inician los registros.", mes));
        }
        if (ym.isAfter(fechaMaxima)) {
            throw new RangoFechaInvalidoException(String.format(
                    "La fecha solicitada ('%s') es posterior a la fecha máxima permitida (%s). No existen registros de telecomunicaciones futuros.", mes, fechaMaxima));
        }
    }

    public void validarAño(int año) {
        int añoMinimo = 2007;
        int añoMaximo = Math.max(2026, Year.now().getValue());
        if (año < añoMinimo) {
            throw new RangoFechaInvalidoException(String.format(
                    "El año solicitado (%d) es anterior a 2007. Los registros oficiales abarcan desde 2007 hasta la actualidad.", año));
        }
        if (año > añoMaximo) {
            throw new RangoFechaInvalidoException(String.format(
                    "El año solicitado (%d) es posterior al año actual (%d). No existen registros futuros.", año, añoMaximo));
        }
    }

    public List<String> obtenerMesesDisponibles() {
        validarBaseDatosNoVacia();
        return registroRepository.findDistinctMeses();
    }

    public String normalizarYValidarOperador(String operador) {
        if (operador == null || operador.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del operador no puede estar vacío.");
        }
        String opTrim = operador.trim();
        if (opTrim.equalsIgnoreCase("Resto operadoras") || opTrim.equalsIgnoreCase("Resto")) {
            return "Resto";
        }

        boolean existe = registroRepository.existsByOperadorIgnoreCase(opTrim);
        if (!existe) {
            throw new OperadorNoEncontradoException(opTrim);
        }
        return opTrim;
    }

    public PaginatedResponse<RegistroDto> obtenerTodos(int pagina, int tamanio) {
        validarBaseDatosNoVacia();
        Pageable pageable = crearPageable(pagina, tamanio);
        Page<RegistroTelecomunicacion> pageResult = registroRepository.findAll(pageable);
        return mapearRespuestaPaginada(pageResult);
    }

    public PaginatedResponse<RegistroDto> obtenerPorOperador(String operador, String mes, Integer año, int pagina, int tamanio) {
        validarBaseDatosNoVacia();
        String operadorNormalizado = normalizarYValidarOperador(operador);

        if (mes != null && !mes.trim().isEmpty()) {
            validarFechaMes(mes);
        }
        if (año != null) {
            validarAño(año);
        }

        Pageable pageable = crearPageable(pagina, tamanio);
        Page<RegistroTelecomunicacion> pageResult;
        String detalleFiltro = "histórico general";

        if (mes != null && !mes.trim().isEmpty()) {
            pageResult = registroRepository.findByOperadorIgnoreCaseAndMes(operadorNormalizado, mes.trim(), pageable);
            detalleFiltro = "mes " + mes;
        } else if (año != null) {
            pageResult = registroRepository.findByOperadorIgnoreCaseAndMesStartingWith(operadorNormalizado, String.valueOf(año), pageable);
            detalleFiltro = "año " + año;
        } else {
            pageResult = registroRepository.findByOperadorIgnoreCase(operadorNormalizado, pageable);
        }

        if (pageResult.isEmpty()) {
            if ("MASORANGE".equalsIgnoreCase(operadorNormalizado) && ((año != null && año < 2024) || (mes != null && mes.compareTo("2024-04") < 0))) {
                throw new SinRegistrosParaOperadorException(
                        "El operador 'MASORANGE' existe en la base de datos, pero no devuelve registros antes de abril de 2024 (inició su actividad tras la fusión de Orange y MásMóvil).");
            }
            if ("Orange".equalsIgnoreCase(operadorNormalizado) && ((año != null && año > 2024) || (mes != null && mes.compareTo("2024-03") > 0))) {
                throw new SinRegistrosParaOperadorException(
                        "El operador 'Orange' existe en la base de datos, pero no devuelve registros después de marzo de 2024 (se integró en MASORANGE).");
            }
            throw new SinRegistrosParaOperadorException(String.format(
                    "El operador '%s' existe en la base de datos, pero no devuelve registros para el período consultado (%s).",
                    operador, detalleFiltro));
        }

        return mapearRespuestaPaginada(pageResult);
    }

    public PaginatedResponse<RegistroDto> obtenerPorMes(String mes, int pagina, int tamanio) {
        validarBaseDatosNoVacia();
        validarFechaMes(mes);
        Pageable pageable = crearPageable(pagina, tamanio);
        Page<RegistroTelecomunicacion> pageResult = registroRepository.findByMes(mes.trim(), pageable);
        return mapearRespuestaPaginada(pageResult);
    }

    public PaginatedResponse<RegistroDto> obtenerPorTecnologia(String tecnologia, int pagina, int tamanio) {
        validarBaseDatosNoVacia();
        if (tecnologia == null || tecnologia.trim().isEmpty()) {
            throw new IllegalArgumentException("La tecnología no puede estar vacía.");
        }
        Pageable pageable = crearPageable(pagina, tamanio);
        Page<RegistroTelecomunicacion> pageResult = registroRepository.findByTecnologiaAccesoIgnoreCase(tecnologia.trim(), pageable);
        if (pageResult.isEmpty()) {
            throw new IllegalArgumentException(String.format("No se encontraron registros para la tecnología '%s'.", tecnologia));
        }
        return mapearRespuestaPaginada(pageResult);
    }

    public PaginatedResponse<RegistroDto> filtrar(String operador, String servicio, String tecnologia,
                                                  String desde, String hasta, int pagina, int tamanio) {
        validarBaseDatosNoVacia();

        String operadorFiltrado = null;
        if (operador != null && !operador.trim().isEmpty()) {
            operadorFiltrado = normalizarYValidarOperador(operador);
        }

        if (desde != null && !desde.trim().isEmpty()) {
            validarFechaMes(desde);
        }
        if (hasta != null && !hasta.trim().isEmpty()) {
            validarFechaMes(hasta);
        }
        if (desde != null && hasta != null && !desde.trim().isEmpty() && !hasta.trim().isEmpty()) {
            if (desde.compareTo(hasta) > 0) {
                throw new RangoFechaInvalidoException("La fecha 'desde' (" + desde + ") no puede ser posterior a 'hasta' (" + hasta + ").");
            }
        }

        final String finalOperador = operadorFiltrado;
        Specification<RegistroTelecomunicacion> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (finalOperador != null) {
                predicates.add(cb.equal(cb.lower(root.get("operador")), finalOperador.toLowerCase()));
            }
            if (servicio != null && !servicio.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("servicio")), servicio.trim().toLowerCase()));
            }
            if (tecnologia != null && !tecnologia.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("tecnologiaAcceso")), tecnologia.trim().toLowerCase()));
            }
            if (desde != null && !desde.trim().isEmpty()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("mes"), desde.trim()));
            }
            if (hasta != null && !hasta.trim().isEmpty()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("mes"), hasta.trim()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = crearPageable(pagina, tamanio);
        Page<RegistroTelecomunicacion> pageResult = registroRepository.findAll(spec, pageable);

        if (pageResult.isEmpty() && finalOperador != null) {
            throw new SinRegistrosParaOperadorException(String.format(
                    "El operador '%s' existe en la base de datos, pero no devuelve registros para los filtros seleccionados.", operador));
        }

        return mapearRespuestaPaginada(pageResult);
    }

    private Pageable crearPageable(int pagina, int tamanio) {
        if (pagina < 0) {
            pagina = 0;
        }
        if (tamanio <= 0) {
            tamanio = 10;
        }
        return PageRequest.of(pagina, tamanio);
    }

    private PaginatedResponse<RegistroDto> mapearRespuestaPaginada(Page<RegistroTelecomunicacion> page) {
        List<RegistroDto> dtos = page.getContent().stream()
                .map(RegistroMapper::toDto)
                .toList();

        return new PaginatedResponse<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
