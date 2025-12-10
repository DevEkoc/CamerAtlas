package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.entities.VerifToken;
import com.devekoc.camerAtlas.enumerations.TokenType;
import com.devekoc.camerAtlas.repositories.VerifTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class VerifTokenService {
    private final VerifTokenRepository verifTokenRepository;
    private final NotificationService notificationService;

    public void generateToken(User user, TokenType tokenType) {
        VerifToken verifToken = new VerifToken();
        verifToken.setType(tokenType);
        verifToken.setUser(user);
        verifToken.setCreation(Instant.now());
        verifToken.setExpiration(Instant.now().plus(10, MINUTES));
        verifToken.setCode(randomCode());
        verifTokenRepository.save(verifToken);
        notificationService.send(verifToken);
    }

    public void saveToken(VerifToken verifToken) {
        verifTokenRepository.save(verifToken);
    }

    private String randomCode() {
        Random random = new Random();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }

    public VerifToken getByCode (String code){
        return verifTokenRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException("Code de vérification invalide ")
        );
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void cleanExpiredVerifTokens() {
    verifTokenRepository.deleteAllByActivationNotNullAndUsed(true); // supprime les jetons utilisés
    verifTokenRepository.deleteAllByExpirationBefore(Instant.now()); // supprime les jetons jamais utilisés mais expirés
    log.info("Verification tokens have been deleted at {}",  Instant.now());
    }
    // inutile quand used = true OU activation != null OU expiration < Instant.now()
}
