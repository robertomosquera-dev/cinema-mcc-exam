package com.mitocode.infrastructure.client;

import com.mitocode.application.dto.ReleaseSeatResponse;
import com.mitocode.application.dto.ReserveSeatResponse;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;
import com.mitocode.application.error.MovieServiceException;
import com.mitocode.application.error.ScreeningUnavailableException;
import com.mitocode.application.port.out.MovieClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientMovie implements MovieClientPort {
    private final WebClient movieServiceWebClient;

    @Value("${movie-service.reserve.max-attempts:3}")
    private int maxAttempts;

    @Value("${movie-service.reserve.retry-delay-ms:500}")
    private long retryDelayMs;

    @Override
    public ScreeningAvailabilityResponse getScreening(UUID screeningId) {
        return movieServiceWebClient.get()
                .uri("/screenings/{id}", screeningId)
                .retrieve()
                .bodyToMono(ScreeningAvailabilityResponse.class)
                .switchIfEmpty(Mono.error(new ScreeningUnavailableException("Movie Service no devolvió la función")))
                .onErrorMap(WebClientException.class,
                        e -> new ScreeningUnavailableException("No se pudo verificar la función en Movie Service", e))
                .block();
    }

    @Override
    public ReserveSeatResponse reserveSeat(UUID screeningId, UUID bookingId) {
        return movieServiceWebClient.patch()
                .uri("/screenings/{id}/reserve", screeningId)
                .header("X-Booking-Id", bookingId.toString())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> response.createException()
                        .map(e -> new ScreeningUnavailableException("Movie Service rechazó la reserva del asiento", e)))
                .onStatus(status -> status.is5xxServerError(), response -> response.createException()
                        .map(e -> new MovieServiceException("Movie Service no pudo reservar el asiento", e)))
                .bodyToMono(ReserveSeatResponse.class)
                .switchIfEmpty(Mono.error(new MovieServiceException("Movie Service no confirmó la reserva", null)))
                .onErrorMap(WebClientException.class,
                        e -> new MovieServiceException("Movie Service no pudo reservar el asiento", e))
                .retryWhen(Retry.fixedDelay(maxAttempts - 1, Duration.ofMillis(retryDelayMs))
                        .filter(MovieServiceException.class::isInstance)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .block();
    }

    @Override
    public ReleaseSeatResponse releaseSeat(UUID screeningId, UUID bookingId) {
        return movieServiceWebClient.patch()
                .uri("/screenings/{id}/release", screeningId)
                .header("X-Booking-Id", bookingId.toString())
                .retrieve()
                .bodyToMono(ReleaseSeatResponse.class)
                .switchIfEmpty(Mono.error(new MovieServiceException("Movie Service no confirmó la liberación", null)))
                .onErrorMap(WebClientException.class,
                        e -> new MovieServiceException("Movie Service no pudo liberar el asiento", e))
                .block();
    }
}
