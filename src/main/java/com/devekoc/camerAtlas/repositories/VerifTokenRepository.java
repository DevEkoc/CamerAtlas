package com.devekoc.camerAtlas.repositories;

import com.devekoc.camerAtlas.entities.VerifToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface VerifTokenRepository extends JpaRepository<VerifToken, Integer> {
    Optional<VerifToken> findByCode(String code);

    void deleteAllByActivationNotNullAndUsed(boolean b);

    void deleteAllByExpirationBefore(Instant expirationBefore);
}
