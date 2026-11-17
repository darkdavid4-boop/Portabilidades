package com.telecom.portabilidades.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class RegistroDto {

    private Long id;
    private String mes;
    private String pais;
    private String servicio;
    private String concepto;
    private String operador;
    private String segmento;
    private String tecnologiaAcceso;
    private String contrato;
    private String terminalTelefoniaMovil;
    private String lineaBaMovil;
    private String tipoBaMayorista;
    private String tipoPortabilidad;
    private String unidades;
    private Long lineas;
    private Double tasaPenetracion;
    private Long portabilidades;

    public RegistroDto() {
    }

    public RegistroDto(Long id, String mes, String pais, String servicio, String concepto, String operador,
                       String segmento, String tecnologiaAcceso, String contrato, String terminalTelefoniaMovil,
                       String lineaBaMovil, String tipoBaMayorista, String tipoPortabilidad, String unidades,
                       Long lineas, Double tasaPenetracion, Long portabilidades) {
        this.id = id;
        this.mes = mes;
        this.pais = pais;
        this.servicio = servicio;
        this.concepto = concepto;
        this.operador = operador;
        this.segmento = segmento;
        this.tecnologiaAcceso = tecnologiaAcceso;
        this.contrato = contrato;
        this.terminalTelefoniaMovil = terminalTelefoniaMovil;
        this.lineaBaMovil = lineaBaMovil;
        this.tipoBaMayorista = tipoBaMayorista;
        this.tipoPortabilidad = tipoPortabilidad;
        this.unidades = unidades;
        this.lineas = lineas;
        this.tasaPenetracion = tasaPenetracion;
        this.portabilidades = portabilidades;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getTecnologiaAcceso() {
        return tecnologiaAcceso;
    }

    public void setTecnologiaAcceso(String tecnologiaAcceso) {
        this.tecnologiaAcceso = tecnologiaAcceso;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getTerminalTelefoniaMovil() {
        return terminalTelefoniaMovil;
    }

    public void setTerminalTelefoniaMovil(String terminalTelefoniaMovil) {
        this.terminalTelefoniaMovil = terminalTelefoniaMovil;
    }

    public String getLineaBaMovil() {
        return lineaBaMovil;
    }

    public void setLineaBaMovil(String lineaBaMovil) {
        this.lineaBaMovil = lineaBaMovil;
    }

    public String getTipoBaMayorista() {
        return tipoBaMayorista;
    }

    public void setTipoBaMayorista(String tipoBaMayorista) {
        this.tipoBaMayorista = tipoBaMayorista;
    }

    public String getTipoPortabilidad() {
        return tipoPortabilidad;
    }

    public void setTipoPortabilidad(String tipoPortabilidad) {
        this.tipoPortabilidad = tipoPortabilidad;
    }

    public String getUnidades() {
        return unidades;
    }

    public void setUnidades(String unidades) {
        this.unidades = unidades;
    }

    public Long getLineas() {
        return lineas;
    }

    public void setLineas(Long lineas) {
        this.lineas = lineas;
    }

    public Double getTasaPenetracion() {
        return tasaPenetracion;
    }

    public void setTasaPenetracion(Double tasaPenetracion) {
        this.tasaPenetracion = tasaPenetracion;
    }

    public Long getPortabilidades() {
        return portabilidades;
    }

    public void setPortabilidades(Long portabilidades) {
        this.portabilidades = portabilidades;
    }
}
