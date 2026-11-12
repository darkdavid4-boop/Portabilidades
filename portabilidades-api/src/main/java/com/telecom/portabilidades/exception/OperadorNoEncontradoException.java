package com.telecom.portabilidades.exception;

public class OperadorNoEncontradoException extends RuntimeException {
    private final String operadorSolicitado;
    private final String operadorAsignado;

    public OperadorNoEncontradoException(String operadorSolicitado) {
        super(String.format("El operador '%s' no está dentro de la base de datos, en cuyo caso ponemos 'Resto operadoras'.", operadorSolicitado));
        this.operadorSolicitado = operadorSolicitado;
        this.operadorAsignado = "Resto operadoras";
    }

    public String getOperadorSolicitado() {
        return operadorSolicitado;
    }

    public String getOperadorAsignado() {
        return operadorAsignado;
    }
}
