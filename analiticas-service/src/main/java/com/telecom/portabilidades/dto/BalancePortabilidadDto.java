package com.telecom.portabilidades.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class BalancePortabilidadDto {

    private String operador;
    private String mes;
    private Long importados;
    private Long exportados;
    private Long balanceNeto;

    public BalancePortabilidadDto() {
    }

    public BalancePortabilidadDto(String operador, String mes, Long importados, Long exportados, Long balanceNeto) {
        this.operador = operador;
        this.mes = mes;
        this.importados = importados;
        this.exportados = exportados;
        this.balanceNeto = balanceNeto;
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

    public Long getImportados() {
        return importados;
    }

    public void setImportados(Long importados) {
        this.importados = importados;
    }

    public Long getExportados() {
        return exportados;
    }

    public void setExportados(Long exportados) {
        this.exportados = exportados;
    }

    public Long getBalanceNeto() {
        return balanceNeto;
    }

    public void setBalanceNeto(Long balanceNeto) {
        this.balanceNeto = balanceNeto;
    }
}
