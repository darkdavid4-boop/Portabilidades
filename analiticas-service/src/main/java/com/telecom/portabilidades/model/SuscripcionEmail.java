package com.telecom.portabilidades.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "suscripciones_email")
public class SuscripcionEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(unique = true, length = 64)
    private String tokenConfirmacion;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenBaja;

    @Column(nullable = false)
    private boolean confirmada;

    @Column(nullable = false)
    private boolean activa;

    private Instant confirmacionExpiraEn;

    @Column(length = 7)
    private String ultimoMesEnviado;

    @Column(nullable = false, updatable = false)
    private Instant creadaEn;

    protected SuscripcionEmail() {
    }

    public SuscripcionEmail(String email, String tokenBaja) {
        this.email = email;
        this.tokenBaja = tokenBaja;
        this.activa = true;
    }

    @PrePersist
    void alCrear() {
        creadaEn = Instant.now();
    }

    public void solicitarConfirmacion(String token, Instant expiraEn, String nuevoTokenBaja) {
        tokenConfirmacion = token;
        confirmacionExpiraEn = expiraEn;
        if (nuevoTokenBaja != null) {
            tokenBaja = nuevoTokenBaja;
        }
        confirmada = false;
        activa = true;
        ultimoMesEnviado = null;
    }

    public void confirmar() {
        confirmada = true;
        tokenConfirmacion = null;
        confirmacionExpiraEn = null;
    }

    public void darDeBaja() {
        activa = false;
        tokenConfirmacion = null;
        confirmacionExpiraEn = null;
    }

    public void marcarMesEnviado(String mes) {
        ultimoMesEnviado = mes;
    }

    public String getEmail() {
        return email;
    }

    public String getTokenConfirmacion() {
        return tokenConfirmacion;
    }

    public String getTokenBaja() {
        return tokenBaja;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public boolean isActiva() {
        return activa;
    }

    public Instant getConfirmacionExpiraEn() {
        return confirmacionExpiraEn;
    }

    public String getUltimoMesEnviado() {
        return ultimoMesEnviado;
    }
}
