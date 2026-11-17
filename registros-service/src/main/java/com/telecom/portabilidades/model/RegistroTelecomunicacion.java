package com.telecom.portabilidades.model;

import jakarta.persistence.*;

@Entity
@Table(name = "registros_telecomunicaciones")
public class RegistroTelecomunicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mes", length = 7)
    private String mes;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "servicio", length = 150)
    private String servicio;

    @Column(name = "concepto", length = 100)
    private String concepto;

    @Column(name = "operador", length = 150)
    private String operador;

    @Column(name = "segmento", length = 150)
    private String segmento;

    @Column(name = "tecnologia_acceso", length = 150)
    private String tecnologiaAcceso;

    @Column(name = "contrato", length = 150)
    private String contrato;

    @Column(name = "terminal_telefonia_movil", length = 150)
    private String terminalTelefoniaMovil;

    @Column(name = "linea_ba_movil", length = 150)
    private String lineaBaMovil;

    @Column(name = "tipo_ba_mayorista", length = 150)
    private String tipoBaMayorista;

    @Column(name = "tipo_portabilidad", length = 50)
    private String tipoPortabilidad;

    @Column(name = "unidades", length = 50)
    private String unidades;

    @Column(name = "lineas")
    private Long lineas;

    @Column(name = "tasa_penetracion")
    private Double tasaPenetracion;

    @Column(name = "portabilidades")
    private Long portabilidades;

    public RegistroTelecomunicacion() {
    }

    public RegistroTelecomunicacion(String mes, String pais, String servicio, String concepto, String operador,
                                   String segmento, String tecnologiaAcceso, String contrato,
                                   String terminalTelefoniaMovil, String lineaBaMovil, String tipoBaMayorista,
                                   String tipoPortabilidad, String unidades, Long lineas, Double tasaPenetracion,
                                   Long portabilidades) {
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
