package com.telecom.portabilidades.service;

import com.telecom.portabilidades.dto.RegistroDto;
import com.telecom.portabilidades.model.RegistroTelecomunicacion;

public class RegistroMapper {

    public static RegistroDto toDto(RegistroTelecomunicacion entity) {
        if (entity == null) {
            return null;
        }
        return new RegistroDto(
                entity.getId(),
                entity.getMes(),
                entity.getPais(),
                entity.getServicio(),
                entity.getConcepto(),
                entity.getOperador(),
                entity.getSegmento(),
                entity.getTecnologiaAcceso(),
                entity.getContrato(),
                entity.getTerminalTelefoniaMovil(),
                entity.getLineaBaMovil(),
                entity.getTipoBaMayorista(),
                entity.getTipoPortabilidad(),
                entity.getUnidades(),
                entity.getLineas(),
                entity.getTasaPenetracion(),
                entity.getPortabilidades()
        );
    }
}
