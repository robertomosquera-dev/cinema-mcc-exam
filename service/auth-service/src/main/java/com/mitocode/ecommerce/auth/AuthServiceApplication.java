package com.mitocode.ecommerce.auth;

import com.mitocode.ecommerce.auth.api.dto.RegisterRequest;
import com.mitocode.ecommerce.auth.application.JWTService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.provisioning.UserDetailsManager;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    @Profile({"default", "docker"})
    CommandLineRunner createDemoUser(JWTService jwtService, UserDetailsManager users) {
        return args -> {
            if (!users.userExists("profesor")) {
                jwtService.register(new RegisterRequest("profesor", "Profesor123!"));
            }
        };
    }
}
