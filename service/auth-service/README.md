# Auth Service

Servicio básico de autenticación del sistema de cine. Escucha en el puerto `50003`.

Arranca Config Server antes que Auth. Desde este directorio, con Java 25:

```bash
../booking-service/mvnw -f pom.xml spring-boot:run
```

La dirección de Config Server se define con `CONFIG_SERVER_URL` (predeterminada `http://localhost:50000`). El puerto y los ajustes JWT están en `auth-service.yml` del repositorio de configuración.

## API

`POST /api/v1/auth/register`:

```json
{"username":"ana","password":"claveSegura123"}
```

Responde `201` con `{"username":"ana"}`. El usuario duplicado produce `409` y los datos inválidos `400`.

`POST /api/v1/auth/login` usa el mismo JSON y responde `200` con `access_token`, `expires_in` y `token_type: Bearer`. Las credenciales incorrectas producen `401`. El cliente envía `Authorization: Bearer <access_token>` al Gateway para llamar las rutas protegidas. Registro y login también están disponibles por Gateway en `http://localhost:8080/api/v1/auth/...`.

Con los perfiles `docker` y predeterminado, Auth crea al iniciar el usuario académico `profesor` con contraseña `Profesor123!` para probar el login sin registro previo.

El token se firma con RS256, incluye `sub` (username), `iss`, `iat` y `exp`. El emisor predeterminado es `http://localhost:50003`, configurable con `AUTH_JWT_ISSUER`. Su vigencia predeterminada es una hora, configurable con `AUTH_JWT_ACCESS_TTL_SECONDS`. La clave pública está en `GET /oauth2/jwks`; para Movie y Booking se deben configurar `issuer-uri` igual al emisor y `jwk-set-uri` apuntando a ese endpoint, con una URL alcanzable desde cada servicio.

Los usuarios registrados viven en memoria y se pierden al reiniciar Auth. El par de claves RSA existente también se genera al arrancar: los tokens emitidos antes del reinicio dejan de validar. Para operación persistente hará falta almacenamiento duradero de usuarios y claves, sin publicar la clave privada.
