package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.RefreshToken;
import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.repositories.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
//@AllArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final long REFRESH_TOKEN_EXPIRATION;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${REFRESH_TOKEN_EXPIRATION}") long REFRESH_TOKEN_EXPIRATION)
    {
        this.refreshTokenRepository = refreshTokenRepository;
        this.REFRESH_TOKEN_EXPIRATION = REFRESH_TOKEN_EXPIRATION;
    }

    /**
     * Crée, stocke et retourne un nouveau Refresh Token pour l'utilisateur.
     * @param user L'utilisateur
     * @return Le RefreshToken créé
     */
    public RefreshToken createRefreshToken(User user) {
        String tokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Valide le token : vérifie s'il existe et s'il n'est pas expiré/révoqué.
     * @param token Le token à vérifier
     * @return Le RefreshToken valide
     */
    public RefreshToken verifyExpiration(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Refresh Token introuvable."));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh Token révoqué.");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new RuntimeException("Refresh Token expiré. Veuillez vous reconnecter.");
        }
        return refreshToken;
    }

    /**
     * Révocation d'un Refresh Token (Logout).
     * @param tokenValue Le jeton à révoquer
     */
    public void revokeToken(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    /**
     * Tâche planifiée pour nettoyer les Refresh Tokens périmés.
     */
    @Scheduled(cron = "0 0 3 * * *") // Chaque jour à 3h00 du matin
    @Transactional
    public void cleanExpiredRefreshTokens() {
        log.info("Nettoyage des Refresh Tokens expirés ou révoqués démarré.");
        refreshTokenRepository.deleteAllByRevokedOrExpiryDateBefore(true, Instant.now());
        log.info("Nettoyage des Refresh Tokens terminé.");
    }

}
