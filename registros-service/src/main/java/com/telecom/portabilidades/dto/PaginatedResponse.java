package com.telecom.portabilidades.dto;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> contenido;
    private int paginaActual;
    private int elementosPorPagina;
    private long totalElementos;
    private int totalPaginas;
    private boolean primeraPagina;
    private boolean ultimaPagina;

    public PaginatedResponse() {
    }

    public PaginatedResponse(List<T> contenido, int paginaActual, int elementosPorPagina,
                             long totalElementos, int totalPaginas, boolean primeraPagina,
                             boolean ultimaPagina) {
        this.contenido = contenido;
        this.paginaActual = paginaActual;
        this.elementosPorPagina = elementosPorPagina;
        this.totalElementos = totalElementos;
        this.totalPaginas = totalPaginas;
        this.primeraPagina = primeraPagina;
        this.ultimaPagina = ultimaPagina;
    }

    public List<T> getContenido() {
        return contenido;
    }

    public void setContenido(List<T> contenido) {
        this.contenido = contenido;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public void setPaginaActual(int paginaActual) {
        this.paginaActual = paginaActual;
    }

    public int getElementosPorPagina() {
        return elementosPorPagina;
    }

    public void setElementosPorPagina(int elementosPorPagina) {
        this.elementosPorPagina = elementosPorPagina;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public void setTotalElementos(long totalElementos) {
        this.totalElementos = totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public boolean isPrimeraPagina() {
        return primeraPagina;
    }

    public void setPrimeraPagina(boolean primeraPagina) {
        this.primeraPagina = primeraPagina;
    }

    public boolean isUltimaPagina() {
        return ultimaPagina;
    }

    public void setUltimaPagina(boolean ultimaPagina) {
        this.ultimaPagina = ultimaPagina;
    }
}
