# Portabilidades

Aplicación Java 21 / Spring Boot 4 formada por dos servicios:

- `registros-service`: importa el CSV, es propietario de PostgreSQL y expone las consultas de registros.
- `analiticas-service`: expone las analíticas y sirve el dashboard; obtiene los datos del servicio de registros por REST.

El navegador usa `analiticas-service` en el puerto 8080. Ese servicio reenvía `/api/registros/**` y consume las páginas de registros por la red privada de Compose; conserva una instantánea en caché durante cinco minutos. `registros-service` escucha en 8081 y PostgreSQL en 5432, pero ninguno de los dos puertos se publica en el host.

En el resumen anual, `totalPortabilidades` cuenta importaciones como eventos para no contar dos veces cada cambio de operador al sumar también las exportaciones.

## Arranque local

Requiere Docker Compose:

```powershell
docker compose up --build
```

El dashboard y la API de analítica quedan en `http://localhost:8080`. Swagger está disponible en `/swagger-ui.html`. El servicio de registros solo se publica en la red privada de Compose. La primera carga del CSV se ejecuta al iniciar `registros-service`; el volumen `postgres-data` conserva la base entre reinicios.

Las credenciales predeterminadas de Compose son solo para desarrollo. Para otros entornos, configura `POSTGRES_USER` y `POSTGRES_PASSWORD` mediante variables de entorno.

## Compilar y probar

Desde la raíz:

```powershell
mvn test
```

El CSV fuente está en `registros-service/src/main/resources/ds_24475_1.csv`. Compose lo monta en modo solo lectura; se puede sustituir configurando `APP_DATA_FILE` con la ruta del fichero del host. Flyway crea el esquema y el importador reemplaza el conjunto de datos de forma transaccional solo cuando cambia su checksum.
