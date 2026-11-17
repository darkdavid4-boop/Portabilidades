package com.telecom.portabilidades.controller;

import com.telecom.portabilidades.dto.PaginatedResponse;
import com.telecom.portabilidades.dto.RegistroDto;
import com.telecom.portabilidades.service.RegistroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registros")
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    /**
     * Devuelve el listado completo de meses disponibles ordenados cronológicamente desc.
     */
    @GetMapping("/meses-disponibles")
    public ResponseEntity<List<String>> obtenerMesesDisponibles() {
        return ResponseEntity.ok(registroService.obtenerMesesDisponibles());
    }

    /**
     * Consulta general de registros con paginación de 10 registros por página por defecto.
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<RegistroDto>> obtenerRegistros(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<RegistroDto> respuesta = registroService.obtenerTodos(page, size);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Consulta registros por operador específico con filtro opcional por mes o año.
     * Ejemplo: /api/registros/operador/MASORANGE?año=2024
     */
    @GetMapping("/operador/{nombreOperador}")
    public ResponseEntity<PaginatedResponse<RegistroDto>> obtenerPorOperador(
            @PathVariable String nombreOperador,
            @RequestParam(required = false) String mes,
            @RequestParam(required = false) Integer año,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<RegistroDto> respuesta = registroService.obtenerPorOperador(nombreOperador, mes, año, page, size);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Consulta la foto fija del mercado en un mes específico (ej: 2024-06).
     */
    @GetMapping("/mes/{yearMonth}")
    public ResponseEntity<PaginatedResponse<RegistroDto>> obtenerPorMes(
            @PathVariable String yearMonth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<RegistroDto> respuesta = registroService.obtenerPorMes(yearMonth, page, size);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Consulta registros según tecnología de acceso (FTTH, xDSL, HFC, etc.).
     */
    @GetMapping("/tecnologia/{tipo}")
    public ResponseEntity<PaginatedResponse<RegistroDto>> obtenerPorTecnologia(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<RegistroDto> respuesta = registroService.obtenerPorTecnologia(tipo, page, size);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Búsqueda dinámica multicriterio con paginación.
     */
    @GetMapping("/filtrar")
    public ResponseEntity<PaginatedResponse<RegistroDto>> filtrarRegistros(
            @RequestParam(required = false) String operador,
            @RequestParam(required = false) String servicio,
            @RequestParam(required = false) String tecnologia,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<RegistroDto> respuesta = registroService.filtrar(operador, servicio, tecnologia, desde, hasta, page, size);
        return ResponseEntity.ok(respuesta);
    }
}
