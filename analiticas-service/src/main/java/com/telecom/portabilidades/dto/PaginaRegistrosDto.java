package com.telecom.portabilidades.dto;

import java.util.List;

public record PaginaRegistrosDto<T>(
        List<T> contenido,
        int paginaActual,
        int elementosPorPagina,
        long totalElementos,
        int totalPaginas,
        boolean primeraPagina,
        boolean ultimaPagina
) {
}
