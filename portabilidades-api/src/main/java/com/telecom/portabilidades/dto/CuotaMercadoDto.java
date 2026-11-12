package com.telecom.portabilidades.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class CuotaMercadoDto {

    private String operador;
    private String mes;
    private Long lineasBandaAncha;
    private Double porcentajeCuota;

    public CuotaMercadoDto() {
    }

    public CuotaMercadoDto(String operador, String mes, Long lineasBandaAncha, Double porcentajeCuota) {
        this.operador = operador;
        this.mes = mes;
        this.lineasBandaAncha = lineasBandaAncha;
        this.porcentajeCuota = porcentajeCuota;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Long getLineasBandaAncha() {
        return lineasBandaAncha;
    }

    public void setLineasBandaAncha(Long lineasBandaAncha) {
        this.lineasBandaAncha = lineasBandaAncha;
    }

    public Double getPorcentajeCuota() {
        return porcentajeCuota;
    }

    public void setPorcentajeCuota(Double porcentajeCuota) {
        this.porcentajeCuota = porcentajeCuota;
    }
}
