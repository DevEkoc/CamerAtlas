package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.VerifToken;
import com.devekoc.camerAtlas.enumerations.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
    private final JavaMailSender javaMailSender;
    private final String EMAIL_USERNAME;

    public NotificationService(JavaMailSender javaMailSender,
                               @Value("${SMTP_MAIL_USERNAME}")
                               String EMAIL_USERNAME) {
        this.javaMailSender = javaMailSender;
        this.EMAIL_USERNAME = EMAIL_USERNAME;
    }

    public void send(VerifToken verifToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(EMAIL_USERNAME);
        mailMessage.setTo(verifToken.getUser().getEmail());
        String message = null;

        switch (verifToken.getType()) {
            case TokenType.ACTIVATION -> {
                mailMessage.setSubject("Code d'activation de votre compte");
                message = String.format("Bonjour %s. " +
                                "Votre code pour l'activation de votre compte CamerAtlas est : --%s--. " +
                                "Ce code expirera dans 10 minutes.",
                        verifToken.getUser().getName(), verifToken.getCode()
                );
            }

            case TokenType.RESET -> {
                mailMessage.setSubject("Code de réinitialisation du mot de passe de votre compte");
                message = String.format("Bonjour %s. " +
                                "Votre code pour la réinitialisation du mot de passe de votre compte CamerAtlas est : --%s--. " +
                                "Ce code expirera dans 10 minutes.",
                        verifToken.getUser().getName(), verifToken.getCode()
                );
            }
        }

        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }

}
