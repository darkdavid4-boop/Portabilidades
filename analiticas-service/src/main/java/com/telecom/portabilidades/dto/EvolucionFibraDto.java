package com.telecom.portabilidades.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class EvolucionFibraDto {

    private String año;
    private Long lineasFtth;
    private Long lineasXdsl;
    private Long lineasHfc;
    private Long totalBandaAnchaFija;
    private Double porcentajeFibra;

    public EvolucionFibraDto() {
    }

    public EvolucionFibraDto(String año, Long lineasFtth, Long lineasXdsl, Long lineasHfc, Long totalBandaAnchaFija, Double porcentajeFibra) {
        this.año = año;
        this.lineasFtth = lineasFtth;
        this.lineasXdsl = lineasXdsl;
        this.lineasHfc = lineasHfc;
        this.totalBandaAnchaFija = totalBandaAnchaFija;
        this.porcentajeFibra = porcentajeFibra;
    }

    public String getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = año;
    }

    public Long getLineasFtth() {
        return lineasFtth;
    }

    public void setLineasFtth(Long lineasFtth) {
        this.lineasFtth = lineasFtth;
    }

    public Long getLineasXdsl() {
        return lineasXdsl;
    }

    public void setLineasXdsl(Long lineasXdsl) {
        this.lineasXdsl = lineasXdsl;
    }

    public Long getLineasHfc() {
        return lineasHfc;
    }

    public void setLineasHfc(Long lineasHfc) {
        this.lineasHfc = lineasHfc;
    }

    public Long getTotalBandaAnchaFija() {
        return totalBandaAnchaFija;
    }

    public void setTotalBandaAnchaFija(Long totalBandaAnchaFija) {
        this.totalBandaAnchaFija = totalBandaAnchaFija;
    }

    public Double getPorcentajeFibra() {
        return porcentajeFibra;
    }

    public void setPorcentajeFibra(Double porcentajeFibra) {
        this.porcentajeFibra = porcentajeFibra;
    }
}
