package com.mitocode.infrastructure.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class MovieScreeningControllerTest {

    private MockMvc mvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUpMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/").with(user("movie-test")))
                .build();
    }

    private final List<UUID> createdMovieIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        for (UUID movieId : createdMovieIds) {
            jdbc.update("DELETE FROM screenings WHERE movie_id = ?", movieId);
            jdbc.update("DELETE FROM movies WHERE id = ?", movieId);
        }
    }

    @Test
    void movieWithScreeningsCannotBeDeletedButMovieWithoutScreeningsCan() throws Exception {
        UUID linkedMovieId = createMovie();
        createScreening(linkedMovieId, 1, 1);

        mvc.perform(delete("/api/v1/movies/{id}", linkedMovieId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Película con funciones"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.instance").value("/api/v1/movies/" + linkedMovieId));

        UUID emptyMovieId = createMovie();
        mvc.perform(delete("/api/v1/movies/{id}", emptyMovieId))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/movies/{id}", emptyMovieId))
                .andExpect(status().isNotFound());
    }

    @Test
    void screeningRejectsInvalidCapacityAndProtectsBothSeatBounds() throws Exception {
        UUID movieId = createMovie();

        mvc.perform(post("/api/v1/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(screeningPayload(movieId, 1, 2)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regla de negocio incumplida"));

        UUID screeningId = createScreening(movieId, 1, 1);
        mvc.perform(get("/api/v1/screenings/{id}", screeningId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieTitle").value("Película de prueba"))
                .andExpect(jsonPath("$.availableSeats").value(1));

        mvc.perform(patch("/api/v1/screenings/{id}/reserve", screeningId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(0));
        mvc.perform(patch("/api/v1/screenings/{id}/reserve", screeningId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Asientos no disponibles"));
        mvc.perform(patch("/api/v1/screenings/{id}/release", screeningId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(1));
        mvc.perform(patch("/api/v1/screenings/{id}/release", screeningId))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void invalidRequestReturnsProblemDetailAndMissingScreeningReturnsNotFound() throws Exception {
        mvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","director":"Directora de prueba","genre":"DRAMA",
                                 "durationMin":90,"rating":"PG"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Solicitud inválida"))
                .andExpect(jsonPath("$.detail").value("El título es obligatorio"))
                .andExpect(jsonPath("$.instance").value("/api/v1/movies"));

        mvc.perform(get("/api/v1/screenings/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"));
    }

    @Test
    void concurrentReservationsCannotSellTheSameSeatTwice() throws Exception {
        UUID screeningId = createScreening(createMovie(), 1, 1);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            Future<Integer> first = executor.submit(() -> reserveAfterSignal(screeningId, start));
            Future<Integer> second = executor.submit(() -> reserveAfterSignal(screeningId, start));
            start.countDown();

            List<Integer> statuses = List.of(first.get(), second.get());
            assertTrue(statuses.contains(200), statuses.toString());
            assertTrue(statuses.contains(422), statuses.toString());
        }

        mvc.perform(get("/api/v1/screenings/{id}", screeningId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(0));
    }

    @Test
    void openApiContainsDocumentedDtoProperties() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.CreateMovieRequest.properties.title.description")
                        .value("Título de la película"))
                .andExpect(jsonPath("$.components.schemas.CreateScreeningRequest.properties.totalSeats.example")
                        .value(80))
                .andExpect(jsonPath("$.components.schemas.ScreeningAvailabilityResponse.properties.movieTitle.description")
                        .value("Título de la película para la reserva"));
    }

    private int reserveAfterSignal(UUID screeningId, CountDownLatch start) throws Exception {
        start.await();
        return mvc.perform(patch("/api/v1/screenings/{id}/reserve", screeningId))
                .andReturn().getResponse().getStatus();
    }

    private UUID createMovie() throws Exception {
        MvcResult result = mvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Película de prueba","director":"Directora de prueba",
                                 "genre":"DRAMA","durationMin":90,"rating":"PG"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        UUID id = idFromLocation(result);
        createdMovieIds.add(id);
        return id;
    }

    private UUID createScreening(UUID movieId, int totalSeats, int availableSeats) throws Exception {
        MvcResult result = mvc.perform(post("/api/v1/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(screeningPayload(movieId, totalSeats, availableSeats)))
                .andExpect(status().isCreated())
                .andReturn();
        return idFromLocation(result);
    }

    private String screeningPayload(UUID movieId, int totalSeats, int availableSeats) {
        return """
                {"movieId":"%s","room":"Sala de prueba","showAt":"2030-01-01T18:00:00",
                 "totalSeats":%d,"availableSeats":%d}
                """.formatted(movieId, totalSeats, availableSeats);
    }

    private UUID idFromLocation(MvcResult result) {
        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);
        String path = URI.create(location).getPath();
        return UUID.fromString(path.substring(path.lastIndexOf('/') + 1));
    }
}
