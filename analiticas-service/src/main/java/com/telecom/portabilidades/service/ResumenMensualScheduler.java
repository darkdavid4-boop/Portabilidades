package com.telecom.portabilidades.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResumenMensualScheduler {

    private final NotificacionEmailService notificacionEmailService;

    public ResumenMensualScheduler(NotificacionEmailService notificacionEmailService) {
        this.notificacionEmailService = notificacionEmailService;
    }

    @Scheduled(
            cron = "${notifications.digest.cron}",
            zone = "${notifications.digest.zone}"
    )
    public void enviarResumenesDisponibles() {
        notificacionEmailService.enviarResumenesMensuales();
    }
}
