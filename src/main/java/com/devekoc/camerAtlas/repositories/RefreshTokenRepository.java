package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.RefreshToken;
import com.devekoc.camerAtlas.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    // Pour la gestion des sessions (ex: déconnecter toutes les sessions d'un utilisateur)
    void deleteAllByUser(User user);

    // Pour le nettoyage programmé des jetons expirés ou révoqués.
    void deleteAllByRevokedOrExpiryDateBefore(boolean revoked, Instant expiryDate);

}
