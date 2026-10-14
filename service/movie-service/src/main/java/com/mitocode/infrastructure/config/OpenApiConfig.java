package com.mitocode.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI movieApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Movie Service API")
                                .description("Catálogo de películas, funciones y disponibilidad de asientos")
                                .version("1.0.0")
                );
    }

}

