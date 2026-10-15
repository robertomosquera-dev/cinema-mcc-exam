package com.mitocode.infrastructure.client;

import com.mitocode.application.dto.ReleaseSeatResponse;
import com.mitocode.application.dto.ReserveSeatResponse;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;
import com.mitocode.application.error.MovieServiceException;
import com.mitocode.application.error.ScreeningUnavailableException;
import com.mitocode.application.port.out.MovieClientPort;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientMovie implements MovieClientPort {
    private final RestClient movieServiceRestClient;

    @Override
    public ScreeningAvailabilityResponse getScreening(UUID screeningId) {
        try {
            ScreeningAvailabilityResponse response = movieServiceRestClient.get()
                    .uri("/screenings/{id}", screeningId)
                    .retrieve().body(ScreeningAvailabilityResponse.class);
            if (response == null) {
                throw new ScreeningUnavailableException("Movie Service no devolvió la función");
            }
            return response;
        } catch (RestClientException e) {
            throw new ScreeningUnavailableException("No se pudo verificar la función en Movie Service", e);
        }
    }

    @Override
    @Retry(name = "movieReserve")
    public ReserveSeatResponse reserveSeat(UUID screeningId, UUID bookingId) {
        try {
            ReserveSeatResponse response = movieServiceRestClient.patch()
                    .uri("/screenings/{id}/reserve", screeningId)
                    .header("X-Booking-Id", bookingId.toString())
                    .retrieve().body(ReserveSeatResponse.class);
            if (response == null) {
                throw new MovieServiceException("Movie Service no confirmó la reserva", null);
            }
            return response;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new ScreeningUnavailableException("Movie Service rechazó la reserva del asiento", e);
            }
            throw new MovieServiceException("Movie Service no pudo reservar el asiento", e);
        } catch (RestClientException e) {
            throw new MovieServiceException("Movie Service no pudo reservar el asiento", e);
        }
    }

    @Override
    public ReleaseSeatResponse releaseSeat(UUID screeningId, UUID bookingId) {
        try {
            ReleaseSeatResponse response = movieServiceRestClient.patch()
                    .uri("/screenings/{id}/release", screeningId)
                    .header("X-Booking-Id", bookingId.toString())
                    .retrieve().body(ReleaseSeatResponse.class);
            if (response == null) {
                throw new MovieServiceException("Movie Service no confirmó la liberación", null);
            }
            return response;
        } catch (RestClientException e) {
            throw new MovieServiceException("Movie Service no pudo liberar el asiento", e);
        }
    }
}
