package com.mitocode.ecommerce.auth.application;

import com.mitocode.ecommerce.auth.api.dto.RegisterRequest;
import com.mitocode.ecommerce.auth.api.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Component
public class JWTService {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${auth.jwt.issuer}")
    private String issuer;

    @Value("${auth.jwt.access-token-ttl}")
    private long accessTokenTtl;

    public JWTService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder,
                      JwtEncoder jwtEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public synchronized void register(RegisterRequest request) {
        if (userDetailsManager.userExists(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }
        UserDetails user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .authorities(List.of())
                .build();
        userDetailsManager.createUser(user);
    }

    public TokenResponse generateToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenTtl))
                .build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return TokenResponse.bearer(token, accessTokenTtl);
    }

    // Conserva el perfil GitHub existente cuando se activa explícitamente.
    public TokenResponse generateTokensFromGitHub(OAuth2User oauth2User) {
        String username = oauth2User.getAttribute("login");
        if (username == null || username.isBlank()) {
            username = "github_user_" + oauth2User.getAttribute("id");
        }
        return generateToken(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                username, null));
    }
}
