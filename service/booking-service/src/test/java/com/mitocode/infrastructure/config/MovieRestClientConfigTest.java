package com.mitocode.infrastructure.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class MovieRestClientConfigTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void forwardsCurrentRequestBearerToMovie() {
        Jwt jwt = Jwt.withTokenValue("signed.test.token")
                .header("alg", "RS256")
                .claim("sub", "test-user")
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient client = new MovieRestClientConfig()
                .movieServiceRestClient(builder, "http://movie-service/api/v1");
        server.expect(requestTo("http://movie-service/api/v1/screenings/123"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer signed.test.token"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        client.get().uri("/screenings/{id}", "123").retrieve().toBodilessEntity();

        server.verify();
    }
}
