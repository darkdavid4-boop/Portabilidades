package com.telecom.portabilidades.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class ErrorResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensaje;
    private String ruta;
    private String operadorAsignado;

    public ErrorResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDto(int status, String error, String mensaje, String ruta) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.ruta = ruta;
    }

    public ErrorResponseDto(int status, String error, String mensaje, String ruta, String operadorAsignado) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.ruta = ruta;
        this.operadorAsignado = operadorAsignado;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getOperadorAsignado() {
        return operadorAsignado;
    }

    public void setOperadorAsignado(String operadorAsignado) {
        this.operadorAsignado = operadorAsignado;
    }
}
