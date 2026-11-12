package com.telecom.portabilidades.controller;

import com.telecom.portabilidades.dto.*;
import com.telecom.portabilidades.service.AnaliticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analiticas")
public class AnaliticaController {

    private final AnaliticaService analiticaService;

    public AnaliticaController(AnaliticaService analiticaService) {
        this.analiticaService = analiticaService;
    }

    /**
     * Calcula el balance neto de portabilidades (Importados - Exportados) por operador en un mes determinado.
     * Ejemplo: /api/analiticas/portabilidades/balance-neto?mes=2024-05
     */
    @GetMapping("/portabilidades/balance-neto")
    public ResponseEntity<List<BalancePortabilidadDto>> obtenerBalanceNeto(@RequestParam String mes) {
        List<BalancePortabilidadDto> balance = analiticaService.obtenerBalanceNetoPortabilidades(mes);
        return ResponseEntity.ok(balance);
    }

    /**
     * Ranking anual de operadores según el volumen de portabilidades móviles importadas/ganadas.
     * Ejemplo: /api/analiticas/portabilidades/ranking?año=2023
     */
    @GetMapping("/portabilidades/ranking")
    public ResponseEntity<List<RankingPortabilidadDto>> obtenerRankingPortabilidades(@RequestParam int año) {
        List<RankingPortabilidadDto> ranking = analiticaService.obtenerRankingPortabilidades(año);
        return ResponseEntity.ok(ranking);
    }

    /**
     * Evolución histórica del despliegue y adopción de la fibra óptica (FTTH) frente a xDSL y cable HFC.
     */
    @GetMapping("/fibra/evolucion")
    public ResponseEntity<List<EvolucionFibraDto>> obtenerEvolucionFibra() {
        List<EvolucionFibraDto> evolucion = analiticaService.obtenerEvolucionFibra();
        return ResponseEntity.ok(evolucion);
    }

    /**
     * Cuota de mercado de cada operador en banda ancha fija minorista para un mes determinado.
     * Ejemplo: /api/analiticas/cuota-mercado/banda-ancha?mes=2024-01
     */
    @GetMapping("/cuota-mercado/banda-ancha")
    public ResponseEntity<List<CuotaMercadoDto>> obtenerCuotaMercadoBandaAncha(@RequestParam String mes) {
        List<CuotaMercadoDto> cuotas = analiticaService.obtenerCuotaMercadoBandaAncha(mes);
        return ResponseEntity.ok(cuotas);
    }

    /**
     * Resumen anual agregado con los totales de líneas fijas, móviles, fibra y portabilidades totales del año.
     * Ejemplo: /api/analiticas/resumen-anual/2023
     */
    @GetMapping("/resumen-anual/{year}")
    public ResponseEntity<ResumenAnualDto> obtenerResumenAnual(@PathVariable int year) {
        ResumenAnualDto resumen = analiticaService.obtenerResumenAnual(year);
        return ResponseEntity.ok(resumen);
    }

    /**
     * Listado completo de años disponibles ordenados cronológicamente desc.
     * Ejemplo: /api/analiticas/anios-disponibles
     */
    @GetMapping("/anios-disponibles")
    public ResponseEntity<List<String>> obtenerAñosDisponibles() {
        return ResponseEntity.ok(analiticaService.obtenerAñosDisponibles());
    }
}
