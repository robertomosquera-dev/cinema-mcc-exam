package com.mitocode.ecommerce.auth;

import com.nimbusds.jose.jwk.JWKSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthHttpTest {
    @Value("${local.server.port}")
    private int port;

    @org.springframework.beans.factory.annotation.Autowired
    private InMemoryUserDetailsManager users;

    @org.springframework.beans.factory.annotation.Autowired
    private PasswordEncoder encoder;

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();

    @Test
    void registrationLoginAndPublicKeyWorkOverHttp() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().replace("-", "");
        String password = "pruebaSegura123";
        String registration = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

        var registered = post("/api/v1/auth/register", registration);
        assertEquals(201, registered.statusCode());
        assertEquals(username, json.readTree(registered.body()).get("username").asText());
        assertFalse(registered.body().contains(password));

        String storedHash = users.loadUserByUsername(username).getPassword();
        assertTrue(encoder.matches(password, storedHash));
        assertNotEquals(password, storedHash);
        assertFalse(registered.body().contains(storedHash));

        assertEquals(409, post("/api/v1/auth/register", registration).statusCode());
        var invalidRegister = post("/api/v1/auth/register", "{\"username\":\"\",\"password\":\"123\"}");
        assertEquals(400, invalidRegister.statusCode());
        assertFalse(invalidRegister.body().contains("123"));
        assertEquals(400, post("/api/v1/auth/login", "{\"username\":\"\",\"password\":\"\"}").statusCode());

        var badLogin = post("/api/v1/auth/login",
                "{\"username\":\"" + username + "\",\"password\":\"incorrecta\"}");
        assertEquals(401, badLogin.statusCode());
        assertFalse(badLogin.body().contains(storedHash));

        var login = post("/api/v1/auth/login", registration);
        assertEquals(200, login.statusCode());
        var response = json.readTree(login.body());
        assertEquals("Bearer", response.get("token_type").asText());
        assertEquals(3600, response.get("expires_in").asLong());
        assertNull(response.get("refresh_token"));
        assertFalse(login.body().contains(password));
        assertFalse(login.body().contains(storedHash));

        var jwksResponse = get("/oauth2/jwks");
        assertEquals(200, jwksResponse.statusCode());
        JWKSet publicKeys = JWKSet.parse(jwksResponse.body());
        assertFalse(publicKeys.getKeys().isEmpty());
        assertTrue(publicKeys.getKeys().stream().noneMatch(key -> key.isPrivate()));

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(baseUrl() + "/oauth2/jwks").build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("http://localhost:50003"));
        Jwt jwt = decoder.decode(response.get("access_token").asText());
        assertEquals(username, jwt.getSubject());
        assertEquals("http://localhost:50003", jwt.getIssuer().toString());
        assertEquals("RS256", jwt.getHeaders().get("alg"));
        assertTrue(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()).toSeconds() >= 3599);
    }

    private HttpResponse<String> post(String path, String body) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create(baseUrl() + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create(baseUrl() + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
