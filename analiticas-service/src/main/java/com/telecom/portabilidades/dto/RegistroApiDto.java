package com.telecom.portabilidades.dto;

public record RegistroApiDto(
        Long id,
        String mes,
        String pais,
        String servicio,
        String concepto,
        String operador,
        String segmento,
        String tecnologiaAcceso,
        String contrato,
        String terminalTelefoniaMovil,
        String lineaBaMovil,
        String tipoBaMayorista,
        String tipoPortabilidad,
        String unidades,
        Long lineas,
        Double tasaPenetracion,
        Long portabilidades
) {
    public String getMes() {
        return mes;
    }

    public String getServicio() {
        return servicio;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getOperador() {
        return operador;
    }

    public String getTecnologiaAcceso() {
        return tecnologiaAcceso;
    }

    public String getTipoPortabilidad() {
        return tipoPortabilidad;
    }

    public Long getLineas() {
        return lineas;
    }

    public Long getPortabilidades() {
        return portabilidades;
    }
}
