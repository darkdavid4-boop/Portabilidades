package com.telecom.portabilidades.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class ResumenAnualDto {

    private int año;
    private Long totalLineasFijas;
    private Long totalLineasMoviles;
    private Long totalLineasFibra;
    private Long totalPortabilidades;

    public ResumenAnualDto() {
    }

    public ResumenAnualDto(int año, Long totalLineasFijas, Long totalLineasMoviles, Long totalLineasFibra, Long totalPortabilidades) {
        this.año = año;
        this.totalLineasFijas = totalLineasFijas;
        this.totalLineasMoviles = totalLineasMoviles;
        this.totalLineasFibra = totalLineasFibra;
        this.totalPortabilidades = totalPortabilidades;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public Long getTotalLineasFijas() {
        return totalLineasFijas;
    }

    public void setTotalLineasFijas(Long totalLineasFijas) {
        this.totalLineasFijas = totalLineasFijas;
    }

    public Long getTotalLineasMoviles() {
        return totalLineasMoviles;
    }

    public void setTotalLineasMoviles(Long totalLineasMoviles) {
        this.totalLineasMoviles = totalLineasMoviles;
    }

    public Long getTotalLineasFibra() {
        return totalLineasFibra;
    }

    public void setTotalLineasFibra(Long totalLineasFibra) {
        this.totalLineasFibra = totalLineasFibra;
    }

    public Long getTotalPortabilidades() {
        return totalPortabilidades;
    }

    public void setTotalPortabilidades(Long totalPortabilidades) {
        this.totalPortabilidades = totalPortabilidades;
    }
}
