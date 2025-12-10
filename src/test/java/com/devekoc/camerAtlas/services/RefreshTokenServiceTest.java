package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.RefreshToken;
import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.repositories.RefreshTokenRepository;
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
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    private final long EXPIRATION = 3600000L; // 1h
    private User user;

    @BeforeEach
    void init() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, EXPIRATION);

        user = new User();
        user.setId(1);
        user.setName("Denver");
        user.setEmail("denver@mail.com");
    }

    // ---------------------------------------------------------------------
    // createRefreshToken()
    // ---------------------------------------------------------------------

    @Test
    void createRefreshToken_shouldCreateAndSaveToken() {
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(user);

        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.isRevoked()).isFalse();
        assertThat(token.getToken()).isNotBlank();
        assertThat(token.getExpiryDate()).isAfter(Instant.now());

        verify(refreshTokenRepository).save(any());
    }

    // ---------------------------------------------------------------------
    // verifyExpiration() — token normal
    // ---------------------------------------------------------------------

    @Test
    void verifyExpiration_shouldReturnToken_whenValid() {
        RefreshToken token = RefreshToken.builder()
                .token("ABC")
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .revoked(false)
                .user(user)
                .build();

        when(refreshTokenRepository.findByToken("ABC")).thenReturn(Optional.of(token));

        RefreshToken result = refreshTokenService.verifyExpiration("ABC");

        assertThat(result).isEqualTo(token);
    }

    // ---------------------------------------------------------------------
    // verifyExpiration() — token révoqué
    // ---------------------------------------------------------------------

    @Test
    void verifyExpiration_shouldThrow_whenRevoked() {
        RefreshToken token = RefreshToken.builder()
                .token("ABC")
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .revoked(true)
                .user(user)
                .build();

        when(refreshTokenRepository.findByToken("ABC")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration("ABC"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("révoqué");
    }

    // ---------------------------------------------------------------------
    // verifyExpiration() — token expiré
    // ---------------------------------------------------------------------

    @Test
    void verifyExpiration_shouldRevokeAndThrow_whenExpired() {
        RefreshToken token = RefreshToken.builder()
                .token("ABC")
                .expiryDate(Instant.now().minus(10, ChronoUnit.MINUTES))
                .revoked(false)
                .user(user)
                .build();

        when(refreshTokenRepository.findByToken("ABC")).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration("ABC"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expiré");

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    // ---------------------------------------------------------------------
    // revokeToken()
    // ---------------------------------------------------------------------

    @Test
    void revokeToken_shouldMarkAsRevoked_whenFound() {
        RefreshToken token = RefreshToken.builder()
                .token("ABC")
                .revoked(false)
                .user(user)
                .build();

        when(refreshTokenRepository.findByToken("ABC")).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        refreshTokenService.revokeToken("ABC");

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void revokeToken_shouldDoNothing_whenNotFound() {
        when(refreshTokenRepository.findByToken("XYZ")).thenReturn(Optional.empty());

        refreshTokenService.revokeToken("XYZ");

        verify(refreshTokenRepository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // cleanExpiredRefreshTokens()
    // ---------------------------------------------------------------------

    @Test
    void cleanExpiredRefreshTokens_shouldTriggerRepositoryCleanup() {
        refreshTokenService.cleanExpiredRefreshTokens();

        verify(refreshTokenRepository).deleteAllByRevokedOrExpiryDateBefore(eq(true), any());
    }
}

