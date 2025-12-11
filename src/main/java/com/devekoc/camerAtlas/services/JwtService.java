package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.exceptions.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@Transactional
@Slf4j
public class JwtService {
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    private final long JWT_EXPIRATION;
    private final String JWT_SECRET_KEY;

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public JwtService(
            UserService userService,
            RefreshTokenService refreshTokenService,
            @Value("${JWT_SECRET_KEY}")
            String JWT_SECRET_KEY,
            @Value("${JWT_EXPIRATION}")
            long JWT_EXPIRATION) {
        this.userService = userService;
        this.JWT_SECRET_KEY = JWT_SECRET_KEY;
        this.JWT_EXPIRATION = JWT_EXPIRATION;
        this.refreshTokenService = refreshTokenService;
    }

    public Map<String, String> generate(String username) {
        User user = (User) userService.loadUserByUsername(username);
        String accessToken = generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        userService.setLastConnection(user, Instant.now());
        return Map.of(
                ACCESS_TOKEN, accessToken,
                REFRESH_TOKEN, refreshToken
        );
    }

    /**
     * Méthode privée pour la génération de l'Access Token seul.
     * Utile pour le rafraîchissement sans relog.
     */
    public String generateAccessToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(JWT_EXPIRATION);

        Map<String, Object> claims = Map.of("nom", user.getName());

        return Jwts
                .builder()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .subject(user.getUsername())
                .claims(claims)
                .signWith(getKey())
                .compact();
    }

    public String extractUserName(String token) {
        return getClaims(token, Claims::getSubject);
    }

    private <T> T getClaims(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Jeton JWT expiré: {}", e.getMessage());
            throw new TokenExpiredException("Jeton expiré : " + e.getMessage());
        } catch (SignatureException e) {
            log.error("Jeton JWT invalide: {}", e.getMessage());
            throw new RuntimeException("Jeton invalide" + e.getMessage());
        }
    }

    private Key getKey() {
        byte[] decode = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(decode);
    }
}
