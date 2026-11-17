package com.telecom.portabilidades.exception;

import com.telecom.portabilidades.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AnaliticaExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AnaliticaExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> manejarSolicitud(
            ResponseStatusException exception, HttpServletRequest request) {
        HttpStatusCode status = exception.getStatusCode();
        String error = HttpStatus.resolve(status.value()) == null
                ? "Error de solicitud"
                : HttpStatus.valueOf(status.value()).getReasonPhrase();
        return ResponseEntity.status(status).body(new ErrorResponseDto(
                LocalDateTime.now(), status.value(), error, exception.getReason(), request.getRequestURI()));
    }

    @ExceptionHandler(RegistroServiceUnavailableException.class)
    public ResponseEntity<ErrorResponseDto> manejarServicioNoDisponible(
            RegistroServiceUnavailableException exception, HttpServletRequest request) {
        log.warn("No se pudo consultar registros-service.", exception);
        return respuesta(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de registros no está disponible.",
                request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> manejarErrorInterno(
            Exception exception, HttpServletRequest request) {
        log.error("Error procesando la petición {}", request.getRequestURI(), exception);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado al procesar la solicitud.", request.getRequestURI());
    }

    private ResponseEntity<ErrorResponseDto> respuesta(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(new ErrorResponseDto(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path));
    }
}
