package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.RefreshToken;
import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.exceptions.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    private JwtService jwtService;

    private final String SECRET = Base64.getEncoder().encodeToString("mysecretkeymysecretkeymysecretkeymysecretkeymysecretkeymysecretkeymysecretkeymysecretkey".getBytes());
    private final long EXPIRATION = 3600000L; // 1h

    User user;

    @BeforeEach
    void init() {
        jwtService = new JwtService(
                userService,
                refreshTokenService,
                SECRET,
                EXPIRATION
        );

        user = new User();
        user.setName("Denver");
        user.setEmail("denver@mail.com");
    }

    // ---------------------------------------------------------------------
    // generate()
    // ---------------------------------------------------------------------

    @Test
    void generate_shouldReturnAccessAndRefreshTokens() {
        RefreshToken refresh = new RefreshToken();
        refresh.setToken("REFRESH-TOKEN-123");

        when(userService.loadUserByUsername("denver")).thenReturn(user);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refresh);

        Map<String, String> tokens = jwtService.generate("denver");

        assertThat(tokens).containsKeys("access_token", "refresh_token");
        assertThat(tokens.get("refresh_token")).isEqualTo("REFRESH-TOKEN-123");
    }

    // ---------------------------------------------------------------------
    // generateAccessToken()
    // ---------------------------------------------------------------------

    @Test
    void generateAccessToken_shouldEncodeUserCorrectly() {
        String token = jwtService.generateAccessToken(user);

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("denver@mail.com");
        assertThat(claims.get("nom")).isEqualTo("Denver");
    }

    // ---------------------------------------------------------------------
    // extractUserName()
    // ---------------------------------------------------------------------

    @Test
    void extractUserName_shouldReturnSubject() {
        String token = jwtService.generateAccessToken(user);
        String subject = jwtService.extractUserName(token);

        assertThat(subject).isEqualTo("denver@mail.com");
    }

    // ---------------------------------------------------------------------
    // getAllClaims() : expired token
    // ---------------------------------------------------------------------

    @Test
    void extractUserName_shouldThrow_whenTokenExpired() {
        Instant issued = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant expired = Instant.now().minus(1, ChronoUnit.HOURS);

        String expiredToken = Jwts.builder()
                .subject("denver")
                .issuedAt(Date.from(issued))
                .expiration(Date.from(expired))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .compact();

        assertThatThrownBy(() -> jwtService.extractUserName(expiredToken))
                .isInstanceOf(TokenExpiredException.class);
    }

    // ---------------------------------------------------------------------
    // invalid signature
    // ---------------------------------------------------------------------

    @Test
    void extractUserName_shouldThrow_whenSignatureInvalid() {
        String invalidToken = Jwts.builder()
                .subject("denver")
                .signWith(Keys.hmacShaKeyFor("WRONGSECRET111111111111WRONGSECRET111111111111WRONGSECRET111111111111WRONGSECRET111111111111WRONGSECRET111111111111".getBytes()))
                .compact();

        assertThatThrownBy(() -> jwtService.extractUserName(invalidToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Jeton invalide");
    }
}

