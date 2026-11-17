package com.telecom.portabilidades.exception;

public class RegistroServiceUnavailableException extends RuntimeException {
    public RegistroServiceUnavailableException(Throwable cause) {
        super("El servicio de registros no está disponible.", cause);
    }
}
