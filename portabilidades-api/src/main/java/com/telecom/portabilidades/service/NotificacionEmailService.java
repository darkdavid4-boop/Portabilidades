package com.telecom.portabilidades.service;

import com.telecom.portabilidades.dto.BalancePortabilidadDto;
import com.telecom.portabilidades.model.SuscripcionEmail;
import com.telecom.portabilidades.repository.RegistroRepository;
import com.telecom.portabilidades.repository.SuscripcionEmailRepository;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Service
public class NotificacionEmailService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionEmailService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long HORAS_PARA_CONFIRMAR = 24;

    private final SuscripcionEmailRepository suscripcionRepository;
    private final RegistroRepository registroRepository;
    private final AnaliticaService analiticaService;
    private final JavaMailSender mailSender;
    private final String remitente;
    private final String baseUrl;

    public NotificacionEmailService(
            SuscripcionEmailRepository suscripcionRepository,
            RegistroRepository registroRepository,
            AnaliticaService analiticaService,
            JavaMailSender mailSender,
            @Value("${notifications.email.from:}") String remitente,
            @Value("${notifications.email.base-url}") String baseUrl) {
        this.suscripcionRepository = suscripcionRepository;
        this.registroRepository = registroRepository;
        this.analiticaService = analiticaService;
        this.mailSender = mailSender;
        this.remitente = remitente;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public void solicitarSuscripcion(String email) {
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        Instant ahora = Instant.now();
        SuscripcionEmail suscripcion = suscripcionRepository.findByEmailIgnoreCase(emailNormalizado)
                .orElseGet(() -> new SuscripcionEmail(emailNormalizado, crearToken()));

        if (suscripcion.isActiva() && suscripcion.isConfirmada()) {
            return;
        }
        if (suscripcion.isActiva() && suscripcion.getConfirmacionExpiraEn() != null
                && suscripcion.getConfirmacionExpiraEn().isAfter(ahora)) {
            return;
        }

        String tokenConfirmacion = crearToken();
        suscripcion.solicitarConfirmacion(
                tokenConfirmacion,
                ahora.plusSeconds(HORAS_PARA_CONFIRMAR * 60 * 60),
                !suscripcion.isActiva() ? crearToken() : null
        );
        suscripcionRepository.save(suscripcion);
        enviarConfirmacion(emailNormalizado, tokenConfirmacion);
    }

    public void validarTokenConfirmacion(String token) {
        buscarConfirmable(token);
    }

    @Transactional
    public void confirmarSuscripcion(String token) {
        buscarConfirmable(token).confirmar();
    }

    public void validarTokenBaja(String token) {
        buscarPorTokenBaja(token);
    }

    @Transactional
    public void darDeBaja(String token) {
        buscarPorTokenBaja(token).darDeBaja();
    }

    public void enviarResumenesMensuales() {
        List<String> mesesDisponibles = registroRepository.findDistinctMeses();
        if (mesesDisponibles.isEmpty()) {
            log.info("No se envían resúmenes: todavía no hay meses de datos disponibles.");
            return;
        }

        String ultimoMesDisponible = mesesDisponibles.get(0);
        List<SuscripcionEmail> suscripciones = suscripcionRepository.findByActivaTrueAndConfirmadaTrue();
        for (SuscripcionEmail suscripcion : suscripciones) {
            String ultimoMesEnviado = suscripcion.getUltimoMesEnviado();
            if (ultimoMesEnviado != null && ultimoMesEnviado.compareTo(ultimoMesDisponible) >= 0) {
                continue;
            }

            List<BalancePortabilidadDto> balance =
                    analiticaService.obtenerBalanceNetoPortabilidades(ultimoMesDisponible);
            try {
                enviarResumen(suscripcion, ultimoMesDisponible, balance);
                suscripcion.marcarMesEnviado(ultimoMesDisponible);
                suscripcionRepository.save(suscripcion);
            } catch (MailException ex) {
                log.error("No se pudo enviar el resumen {} a {}.", ultimoMesDisponible,
                        ocultarEmail(suscripcion.getEmail()), ex);
            }
        }
    }

    private SuscripcionEmail buscarConfirmable(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de confirmación no es válido.");
        }
        SuscripcionEmail suscripcion = suscripcionRepository.findByTokenConfirmacion(token)
                .orElseThrow(() -> new IllegalArgumentException("El token de confirmación no es válido o ya fue usado."));
        if (suscripcion.getConfirmacionExpiraEn() == null
                || !suscripcion.getConfirmacionExpiraEn().isAfter(Instant.now())) {
            throw new IllegalArgumentException("El enlace de confirmación ha caducado. Vuelve a solicitar la suscripción.");
        }
        return suscripcion;
    }

    private SuscripcionEmail buscarPorTokenBaja(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de baja no es válido.");
        }
        return suscripcionRepository.findByTokenBaja(token)
                .orElseThrow(() -> new IllegalArgumentException("El token de baja no es válido."));
    }

    private void enviarConfirmacion(String email, String token) {
        String url = crearUrl("/api/notificaciones/confirmar", token);
        enviar(email, "Confirma tu suscripción a Portabilidades",
                "Para confirmar la recepción del resumen mensual de portabilidades, abre este enlace:\n\n"
                        + url + "\n\nEl enlace caduca en 24 horas. Si no solicitaste esta suscripción, ignora este mensaje.");
    }

    private void enviarResumen(
            SuscripcionEmail suscripcion,
            String mes,
            List<BalancePortabilidadDto> balance) {
        StringBuilder cuerpo = new StringBuilder()
                .append("Resumen mensual de portabilidades — ").append(mes).append("\n\n");
        if (balance.isEmpty()) {
            cuerpo.append("No hay datos de balance de portabilidades disponibles para este mes.\n");
        } else {
            NumberFormat formato = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-ES"));
            for (BalancePortabilidadDto operador : balance) {
                cuerpo.append(operador.getOperador())
                        .append(": balance neto ").append(formato.format(operador.getBalanceNeto()))
                        .append(" (").append(formato.format(operador.getImportados())).append(" importados, ")
                        .append(formato.format(operador.getExportados())).append(" exportados)\n");
            }
        }
        cuerpo.append("\nPara dejar de recibir estos correos, abre este enlace:\n")
                .append(crearUrl("/api/notificaciones/baja", suscripcion.getTokenBaja()));
        enviar(suscripcion.getEmail(), "Resumen de portabilidades — " + mes, cuerpo.toString());
    }

    private void enviar(String destinatario, String asunto, String cuerpo) {
        if (remitente == null || remitente.isBlank()) {
            throw new IllegalStateException("Configura NOTIFICATIONS_EMAIL_FROM con una dirección remitente verificada.");
        }
        try {
            new InternetAddress(remitente, true);
        } catch (AddressException ex) {
            throw new IllegalStateException("NOTIFICATIONS_EMAIL_FROM no contiene una dirección válida.", ex);
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }

    private String crearUrl(String ruta, String token) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(ruta)
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
    }

    private String crearToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String ocultarEmail(String email) {
        int arroba = email.indexOf('@');
        if (arroba <= 1) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(arroba);
    }
}
