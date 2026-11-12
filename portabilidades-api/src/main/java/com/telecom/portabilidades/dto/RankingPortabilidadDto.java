package com.telecom.portabilidades.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class RankingPortabilidadDto {

    private int posicion;
    private String operador;
    private String periodo;
    private Long totalImportados;
    private Long totalExportados;
    private Long balanceNeto;

    public RankingPortabilidadDto() {
    }

    public RankingPortabilidadDto(int posicion, String operador, String periodo, Long totalImportados, Long totalExportados, Long balanceNeto) {
        this.posicion = posicion;
        this.operador = operador;
        this.periodo = periodo;
        this.totalImportados = totalImportados;
        this.totalExportados = totalExportados;
        this.balanceNeto = balanceNeto;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Long getTotalImportados() {
        return totalImportados;
    }

    public void setTotalImportados(Long totalImportados) {
        this.totalImportados = totalImportados;
    }

    public Long getTotalExportados() {
        return totalExportados;
    }

    public void setTotalExportados(Long totalExportados) {
        this.totalExportados = totalExportados;
    }

    public Long getBalanceNeto() {
        return balanceNeto;
    }

    public void setBalanceNeto(Long balanceNeto) {
        this.balanceNeto = balanceNeto;
    }
}
