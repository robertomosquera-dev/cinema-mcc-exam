# Reservas de cine

Sistema de películas, funciones y reservas dividido en servicios Java 25. Se ejecuta con Docker Compose y se accede a las API por el Gateway.

## Requisitos

- Docker con el complemento `docker compose`.
- Acceso a Internet para que Config Server descargue sus archivos desde GitHub al arrancar.
- Puerto `8080` disponible en el equipo.

El archivo `.env` de la raíz contiene `POSTGRES_USER` y `POSTGRES_PASSWORD` con valores académicos de ejemplo. Son las únicas variables que hay que configurar para el arranque local.

## Contenedores y funciones

| Contenedor | Función |
| --- | --- |
| `postgres` | PostgreSQL 16 con un volumen persistente para `moviedb` y `bookingdb`. |
| `config-service` | Descarga la configuración desde GitHub y entrega a los servicios el perfil `docker`. |
| `auth-service` | Registra usuarios, inicia sesión y emite JWT firmados con RS256. |
| `movie-service` | Gestiona películas, funciones y disponibilidad de asientos. |
| `booking-service` | Crea, consulta y cancela reservas; coordina la reserva de asientos con Movie. |
| `gateway-service` | Publica las API y Swagger en `localhost:8080`; valida los JWT. |

Solo Gateway publica un puerto al equipo. Los contenedores se comunican usando nombres de servicio y puertos internos. Booking conserva su configuración de instancia estática y balanceo para llamar a Movie. No se usan Eureka, Kafka ni otros componentes de descubrimiento.

Movie y Booking usan bases separadas. El script `docker/init-db/create-databases.sql` las crea **solo al inicializar un volumen PostgreSQL vacío**; Flyway crea y migra las tablas. Auth guarda los usuarios y su par de claves RSA en memoria: al reiniciarlo se pierden los usuarios registrados y los JWT anteriores dejan de ser válidos. El usuario académico `profesor` se vuelve a crear al iniciar Auth con el perfil `docker` o el perfil predeterminado. La clave privada permanece en Auth; Movie y Booking obtienen la clave pública mediante su JWKS.

## Saga de reservas

Booking coordina la creación de una reserva mediante una Saga: verifica la función, crea la reserva pendiente, reserva el asiento en Movie y confirma la reserva. La implementación usa una interfaz genérica para ejecutar una lista de pasos. Un bucle ejecuta los pasos en orden y, si alguno falla, otro bucle recorre los pasos iniciados en orden inverso para compensarlos. La cancelación libera el asiento reservado.

## Arranque y parada

Desde la raíz de este proyecto:

```bash
docker compose up --build -d
docker compose ps
```

Compose espera a que los servicios necesarios estén sanos antes de iniciar los dependientes. Para detener y volver a iniciar:

```bash
docker compose stop
docker compose start
```

`docker compose down` retira los contenedores y conserva el volumen de PostgreSQL. No uses `docker compose down -v` si quieres conservar los datos. Si el volumen ya existía antes de incorporar el script de inicialización, el script no se ejecutará automáticamente.

## Acceso

- Swagger: <http://localhost:8080/swagger>
- OpenAPI Movie: <http://localhost:8080/movie-service/v3/api-docs>
- OpenAPI Booking: <http://localhost:8080/booking-service/v3/api-docs>
- Registro: `POST http://localhost:8080/api/v1/auth/register`
- Login: `POST http://localhost:8080/api/v1/auth/login`

Para probar el login sin registrar un usuario, usa `profesor` / `Profesor123!`. Estas credenciales son solo de ejemplo académico. Tras iniciar sesión, copia el valor `access_token` en **Authorize** de Swagger para llamar a las rutas protegidas. Registro, login, JWKS y documentación son públicos.
