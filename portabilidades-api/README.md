# API de portabilidades

## Notificaciones por email

Las notificaciones envían un resumen mensual del balance de portabilidades por operador. Para suscribirse, la persona debe confirmar su dirección; los correos de resumen incluyen un enlace para cancelar la suscripción.

Configura en el entorno de ejecución las credenciales SMTP de Brevo y una dirección remitente verificada:

| Variable | Descripción |
| --- | --- |
| `BREVO_SMTP_USERNAME` | Usuario SMTP de Brevo |
| `BREVO_SMTP_KEY` | Clave SMTP de Brevo |
| `NOTIFICATIONS_EMAIL_FROM` | Dirección remitente verificada en Brevo |
| `NOTIFICATIONS_BASE_URL` | URL pública de esta API, usada en los enlaces de confirmación y baja |

Opcionalmente, `BREVO_SMTP_HOST` y `BREVO_SMTP_PORT` permiten cambiar el servidor SMTP. El resumen se comprueba cada día a las 09:00 de Madrid y solo se envía cuando hay un mes de datos más reciente que el último enviado a cada suscriptor. Se puede ajustar con `NOTIFICATIONS_DIGEST_CRON` y `NOTIFICATIONS_DIGEST_ZONE`.

### Endpoints

- `POST /api/notificaciones/suscripciones` — recibe `{"email":"persona@ejemplo.com"}` e inicia la confirmación.
- `GET /api/notificaciones/confirmar?token=...` — muestra la confirmación; al pulsar el botón se activa la suscripción.
- `POST /api/notificaciones/confirmar?token=...` — confirma la suscripción.
- `GET /api/notificaciones/baja?token=...` — muestra la página de baja.
- `POST /api/notificaciones/baja?token=...` — cancela la suscripción.

Las suscripciones se conservan en H2 en disco. `DATABASE_URL` permite configurar otra URL JDBC; la carga del CSV reemplaza los registros de análisis al iniciar la API, sin borrar las suscripciones.
