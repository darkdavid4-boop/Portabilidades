package com.telecom.portabilidades.controller;

import com.telecom.portabilidades.dto.MensajeNotificacionDto;
import com.telecom.portabilidades.dto.SuscripcionEmailRequest;
import com.telecom.portabilidades.service.NotificacionEmailService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionEmailService notificacionEmailService;

    public NotificacionController(NotificacionEmailService notificacionEmailService) {
        this.notificacionEmailService = notificacionEmailService;
    }

    @PostMapping("/suscripciones")
    public ResponseEntity<MensajeNotificacionDto> solicitarSuscripcion(
            @Valid @RequestBody SuscripcionEmailRequest solicitud) {
        notificacionEmailService.solicitarSuscripcion(solicitud.email());
        return ResponseEntity.accepted().body(new MensajeNotificacionDto(
                "Si la dirección puede suscribirse, recibirá un correo para confirmar."));
    }

    @GetMapping(value = "/confirmar", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> mostrarConfirmacion(@RequestParam String token) {
        notificacionEmailService.validarTokenConfirmacion(token);
        return ResponseEntity.ok(paginaConfirmacion(
                "Confirmar suscripción",
                "Confirma que quieres recibir el resumen mensual de portabilidades.",
                "/api/notificaciones/confirmar?token=" + token,
                "Confirmar suscripción"));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<MensajeNotificacionDto> confirmarSuscripcion(
            @RequestParam String token) {
        notificacionEmailService.confirmarSuscripcion(token);
        return ResponseEntity.ok(new MensajeNotificacionDto("Suscripción confirmada."));
    }

    @GetMapping(value = "/baja", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> mostrarBaja(@RequestParam String token) {
        notificacionEmailService.validarTokenBaja(token);
        return ResponseEntity.ok(paginaConfirmacion(
                "Cancelar suscripción",
                "Confirma que quieres dejar de recibir los resúmenes mensuales.",
                "/api/notificaciones/baja?token=" + token,
                "Dar de baja"));
    }

    @PostMapping("/baja")
    public ResponseEntity<MensajeNotificacionDto> darDeBaja(
            @RequestParam String token) {
        notificacionEmailService.darDeBaja(token);
        return ResponseEntity.ok(new MensajeNotificacionDto("Suscripción cancelada."));
    }

    private String paginaConfirmacion(String titulo, String texto, String accion, String boton) {
        return """
                <!doctype html>
                <html lang="es">
                <head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
                <title>%s</title></head>
                <body><main><h1>%s</h1><p>%s</p>
                <form method="post" action="%s"><button type="submit">%s</button></form>
                </main></body></html>
                """.formatted(titulo, titulo, texto, accion, boton);
    }
}
