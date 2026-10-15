# Creación de reservas: contrato de integración

`POST /api/v1/bookings` requiere `screeningId`, `customerId` y `totalAmount > 0`. Una reserva ocupa un asiento. Booking consulta `GET /api/v1/screenings/{id}` en Movie, guarda PENDING, reserva el asiento, guarda CONFIRMED y devuelve 201 con `Location`. Si la función no puede verificarse o no tiene asientos, devuelve 422 sin crear la reserva.

Booking llama a `PATCH /api/v1/screenings/{id}/reserve` y `PATCH /api/v1/screenings/{id}/release` con el header `X-Booking-Id: <UUID>`. Movie acepta el header opcionalmente para conservar a los consumidores existentes. Para las llamadas de Booking, `bookingId` identifica una única reserva de asiento: repetir `reserve` no vuelve a descontar; repetir `release` no vuelve a sumar. Si `release` llega antes que un `reserve` retrasado, Movie guarda una marca de liberación e impide el descuento tardío. La tabla `seat_reservations` se crea con la migración V5 de Movie.

Booking reintenta únicamente fallos transitorios de `reserve`, con tres intentos totales y 500 ms entre intentos. Un error HTTP 4xx de Movie no se reintenta. `WebClient` usa directamente `MOVIE_SERVICE_URL`, con valor predeterminado `http://localhost:51100/api/v1` en local y `http://movie-service:51100/api/v1` con el perfil Docker. No se necesita Eureka ni una instancia estática de Spring Cloud.

Cada transición local se persiste en su propia transacción. Si falla la reserva o confirmación, la Saga intenta liberar el asiento y guardar CANCELLED. Si la base de datos está indisponible durante la compensación, no se puede garantizar la eliminación del estado PENDING; el error se registra y se requiere intervención operativa. Tampoco existe una cola de recuperación duradera para compensaciones remotas fallidas.

Las rutas GET, listado y cancelación solicitada por el cliente permanecen fuera de este flujo y sus servicios siguen sin implementación. La URL `Location` se genera, pero su GET todavía no está disponible.
