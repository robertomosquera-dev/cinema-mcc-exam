package com.mitocode.infrastructure.client;

import com.mitocode.application.error.MovieServiceException;
import com.mitocode.application.error.ScreeningUnavailableException;
import com.mitocode.application.port.out.MovieClientPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(classes = ClientMovieRetryTest.TestApplication.class, properties = {
        "resilience4j.retry.instances.movieReserve.max-attempts=3",
        "resilience4j.retry.instances.movieReserve.wait-duration=1ms",
        "resilience4j.retry.instances.movieReserve.retry-exceptions=com.mitocode.application.error.MovieServiceException"
})
class ClientMovieRetryTest {
    @Autowired MovieClientPort client;
    @Autowired MockRestServiceServer movieServer;

    @AfterEach
    void verifyRequests() {
        movieServer.verify();
        movieServer.reset();
    }

    @Test
    void transientErrorsRetryThreeTimesWithSameBookingId() {
        UUID screeningId = UUID.randomUUID(), bookingId = UUID.randomUUID();
        String url = "http://movie-service/api/v1/screenings/" + screeningId + "/reserve";
        for (int i = 0; i < 2; i++) {
            movieServer.expect(requestTo(url)).andExpect(method(HttpMethod.PATCH))
                    .andExpect(header("X-Booking-Id", bookingId.toString()))
                    .andRespond(withStatus(SERVICE_UNAVAILABLE));
        }
        movieServer.expect(requestTo(url)).andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Booking-Id", bookingId.toString()))
                .andRespond(withStatus(OK).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"screeningId\":\"" + screeningId + "\",\"availableSeats\":1}"));

        assertEquals(1, client.reserveSeat(screeningId, bookingId).availableSeats());
    }

    @Test
    void businessErrorIsNotRetried() {
        UUID screeningId = UUID.randomUUID(), bookingId = UUID.randomUUID();
        movieServer.expect(requestTo("http://movie-service/api/v1/screenings/" + screeningId + "/reserve"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY));

        assertThrows(ScreeningUnavailableException.class, () -> client.reserveSeat(screeningId, bookingId));
    }

    @Test
    void transientErrorsStopAfterThreeAttempts() {
        UUID screeningId = UUID.randomUUID(), bookingId = UUID.randomUUID();
        String url = "http://movie-service/api/v1/screenings/" + screeningId + "/reserve";
        for (int i = 0; i < 3; i++) {
            movieServer.expect(requestTo(url)).andRespond(withStatus(SERVICE_UNAVAILABLE));
        }

        assertThrows(MovieServiceException.class, () -> client.reserveSeat(screeningId, bookingId));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            FlywayAutoConfiguration.class
    })
    @Import(ClientMovie.class)
    static class TestApplication {
        @Bean
        MockMovieHttp mockMovieHttp() {
            RestClient.Builder builder = RestClient.builder().baseUrl("http://movie-service/api/v1");
            MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
            return new MockMovieHttp(builder.build(), server);
        }

        @Bean
        RestClient movieServiceRestClient(MockMovieHttp mockMovieHttp) {
            return mockMovieHttp.client();
        }

        @Bean
        MockRestServiceServer movieServer(MockMovieHttp mockMovieHttp) {
            return mockMovieHttp.server();
        }
    }

    private record MockMovieHttp(RestClient client, MockRestServiceServer server) {}
}
