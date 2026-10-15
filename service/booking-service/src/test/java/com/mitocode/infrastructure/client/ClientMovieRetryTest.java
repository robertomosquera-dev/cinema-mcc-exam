package com.mitocode.infrastructure.client;

import com.mitocode.application.error.ScreeningUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ClientMovieRetryTest {
    @Test
    void transientErrorsRetryThreeTimesWithSameBookingId() {
        UUID screeningId = UUID.randomUUID(), bookingId = UUID.randomUUID();
        var attempts = new AtomicInteger();
        WebClient webClient = WebClient.builder().baseUrl("http://movie-service/api/v1")
                .exchangeFunction(request -> {
                    assertEquals(bookingId.toString(), request.headers().getFirst("X-Booking-Id"));
                    assertEquals("/api/v1/screenings/" + screeningId + "/reserve", request.url().getPath());
                    int attempt = attempts.incrementAndGet();
                    return Mono.just(attempt < 3
                            ? ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE).build()
                            : ClientResponse.create(HttpStatus.OK)
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .body("{\"screeningId\":\"" + screeningId + "\",\"availableSeats\":1}").build());
                }).build();
        var client = new ClientMovie(webClient);
        ReflectionTestUtils.setField(client, "maxAttempts", 3);
        ReflectionTestUtils.setField(client, "retryDelayMs", 0L);

        assertEquals(1, client.reserveSeat(screeningId, bookingId).availableSeats());
        assertEquals(3, attempts.get());
    }

    @Test
    void businessErrorIsNotRetried() {
        var attempts = new AtomicInteger();
        WebClient webClient = WebClient.builder().baseUrl("http://movie-service/api/v1")
                .exchangeFunction(request -> {
                    attempts.incrementAndGet();
                    return Mono.just(ClientResponse.create(HttpStatus.UNPROCESSABLE_ENTITY).build());
                }).build();
        var client = new ClientMovie(webClient);
        ReflectionTestUtils.setField(client, "maxAttempts", 3);
        ReflectionTestUtils.setField(client, "retryDelayMs", 0L);

        assertThrows(ScreeningUnavailableException.class,
                () -> client.reserveSeat(UUID.randomUUID(), UUID.randomUUID()));
        assertEquals(1, attempts.get());
    }
}
