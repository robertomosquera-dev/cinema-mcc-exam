# Auth Service

Servicio básico de autenticación del sistema de cine. Escucha en el puerto `50003`.

Desde este directorio, con Java 25:

```bash
../booking-service/mvnw -f pom.xml spring-boot:run
```

Si Eureka no está activo, se puede añadir `-Dspring-boot.run.arguments=--eureka.client.enabled=false`.

## API

`POST /api/v1/auth/register`:

```json
{"username":"ana","password":"claveSegura123"}
```

Responde `201` con `{"username":"ana"}`. El usuario duplicado produce `409` y los datos inválidos `400`.

`POST /api/v1/auth/login` usa el mismo JSON y responde `200` con `access_token`, `expires_in` y `token_type: Bearer`. Las credenciales incorrectas producen `401`. El cliente envía `Authorization: Bearer <access_token>` a los servicios que validen JWT.

El token se firma con RS256, incluye `sub` (username), `iss`, `iat` y `exp`. El emisor predeterminado es `http://localhost:50003`, configurable con `AUTH_JWT_ISSUER`. Su vigencia predeterminada es una hora, configurable con `AUTH_JWT_ACCESS_TTL_SECONDS`. La clave pública está en `GET /oauth2/jwks`; para Movie y Booking se deben configurar `issuer-uri` igual al emisor y `jwk-set-uri` apuntando a ese endpoint, con una URL alcanzable desde cada servicio.

Los usuarios registrados viven en memoria y se pierden al reiniciar Auth. El par de claves RSA existente también se genera al arrancar: los tokens emitidos antes del reinicio dejan de validar. Para operación persistente hará falta almacenamiento duradero de usuarios y claves, sin publicar la clave privada.
