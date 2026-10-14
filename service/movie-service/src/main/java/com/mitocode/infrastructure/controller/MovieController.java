package com.mitocode.infrastructure.controller;

import com.mitocode.application.dto.CreateMovieRequest;
import com.mitocode.application.dto.MovieResponse;
import com.mitocode.application.dto.UpdateMovieRequest;
import com.mitocode.application.port.in.ActivateMovieUseCase;
import com.mitocode.application.port.in.CreateMovieUseCase;
import com.mitocode.application.port.in.DeactivateMovieUseCase;
import com.mitocode.application.port.in.DeleteMovieUseCase;
import com.mitocode.application.port.in.GetMovieUseCase;
import com.mitocode.application.port.in.GetMoviesUseCase;
import com.mitocode.application.port.in.UpdateMovieUseCase;
import com.mitocode.infrastructure.utils.ResourceUriUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(MovieController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Películas")
public class MovieController {

    public static final String BASE_PATH = "/api/v1/movies";
    private static final String RESOURCE_PATH = BASE_PATH + "/{id}";

    private final CreateMovieUseCase createMovieUseCase;
    private final GetMoviesUseCase getMoviesUseCase;
    private final GetMovieUseCase getMovieUseCase;
    private final UpdateMovieUseCase updateMovieUseCase;
    private final DeleteMovieUseCase deleteMovieUseCase;
    private final ActivateMovieUseCase activateMovieUseCase;
    private final DeactivateMovieUseCase deactivateMovieUseCase;

    @PostMapping
    @Operation(summary = "Crear película")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Película creada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody CreateMovieRequest request,
                                                UriComponentsBuilder uriBuilder) {
        MovieResponse response = createMovieUseCase.createMovie(request);
        return ResponseEntity.created(ResourceUriUtils.created(uriBuilder, RESOURCE_PATH, response.id()))
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Listar películas")
    @ApiResponse(responseCode = "200", description = "Películas encontradas")
    public ResponseEntity<List<MovieResponse>> findAll() {
        return ResponseEntity.ok(getMoviesUseCase.getMovies());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar película")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película encontrada"),
            @ApiResponse(responseCode = "404", description = "Película inexistente")
    })
    public ResponseEntity<MovieResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(getMovieUseCase.getMovie(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar película")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película actualizada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Película inexistente"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public ResponseEntity<MovieResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateMovieRequest request) {
        return ResponseEntity.ok(updateMovieUseCase.updateMovie(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar película")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Película eliminada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Película inexistente"),
            @ApiResponse(responseCode = "422", description = "La película tiene funciones asociadas")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteMovieUseCase.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activar película")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película activada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Película inexistente"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public ResponseEntity<MovieResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(activateMovieUseCase.activateMovie(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar película")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película desactivada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Película inexistente"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public ResponseEntity<MovieResponse> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(deactivateMovieUseCase.deactivateMovie(id));
    }
}
