package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.entities.VerifToken;
import com.devekoc.camerAtlas.enumerations.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(javaMailSender, "noreply@cameratlas.com");
    }

    private VerifToken buildToken(TokenType type) {
        User user = new User();
        user.setName("Denver");
        user.setEmail("denver@mail.com");

        VerifToken token = new VerifToken();
        token.setUser(user);
        token.setType(type);
        token.setCode("123456");

        return token;
    }

    // ----------------------------------------------------------------------
    // ACTIVATION
    // ----------------------------------------------------------------------

    @Test
    void send_shouldSendActivationEmail() {
        VerifToken token = buildToken(TokenType.ACTIVATION);

        // Capture du mail envoyé
        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        notificationService.send(token);

        verify(javaMailSender).send(mailCaptor.capture());
        SimpleMailMessage sentMail = mailCaptor.getValue();

        assertThat(sentMail.getFrom()).isEqualTo("noreply@cameratlas.com");
        assertThat(sentMail.getTo()).containsExactly("denver@mail.com");
        assertThat(sentMail.getSubject()).isEqualTo("Code d'activation de votre compte");

        assertThat(sentMail.getText()).contains("Bonjour Denver.");
        assertThat(sentMail.getText()).contains("--123456--");
        assertThat(sentMail.getText()).contains("activation de votre compte CamerAtlas");
    }

    // ----------------------------------------------------------------------
    // RESET PASSWORD
    // ----------------------------------------------------------------------

    @Test
    void send_shouldSendResetEmail() {
        VerifToken token = buildToken(TokenType.RESET);

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        notificationService.send(token);

        verify(javaMailSender).send(mailCaptor.capture());
        SimpleMailMessage sentMail = mailCaptor.getValue();

        assertThat(sentMail.getFrom()).isEqualTo("noreply@cameratlas.com");
        assertThat(sentMail.getTo()).containsExactly("denver@mail.com");
        assertThat(sentMail.getSubject()).isEqualTo("Code de réinitialisation du mot de passe de votre compte");

        assertThat(sentMail.getText()).contains("Bonjour Denver.");
        assertThat(sentMail.getText()).contains("--123456--");
        assertThat(sentMail.getText()).contains("réinitialisation du mot de passe de votre compte CamerAtlas");
    }
}

