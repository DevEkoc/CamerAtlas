package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.entities.VerifToken;
import com.devekoc.camerAtlas.enumerations.TokenType;
import com.devekoc.camerAtlas.repositories.VerifTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;


import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifTokenServiceTest {

    @Mock
    private VerifTokenRepository verifTokenRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private VerifTokenService verifTokenService;

    User user;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(1);
        user.setName("Denver");
        user.setEmail("denver@mail.com");
    }

    // ---------------------------------------------------------------------
    // generateToken()
    // ---------------------------------------------------------------------

    @Test
    void generateToken_shouldCreateAndSendToken() {
        doReturn(null).when(verifTokenRepository).save(any());
        doNothing().when(notificationService).send(any());

        verifTokenService.generateToken(user, TokenType.ACTIVATION);

        ArgumentCaptor<VerifToken> captor = ArgumentCaptor.forClass(VerifToken.class);
        verify(verifTokenRepository).save(captor.capture());

        VerifToken saved = captor.getValue();

        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getType()).isEqualTo(TokenType.ACTIVATION);
        assertThat(saved.getCode()).hasSize(6);
        assertThat(saved.getExpiration()).isAfter(saved.getCreation());

        verify(notificationService).send(any());
    }

    // ---------------------------------------------------------------------
    // saveToken()
    // ---------------------------------------------------------------------

    @Test
    void saveToken_shouldCallRepository() {
        VerifToken vt = new VerifToken();
        verifTokenService.saveToken(vt);

        verify(verifTokenRepository).save(vt);
    }

    // ---------------------------------------------------------------------
    // getByCode()
    // ---------------------------------------------------------------------

    @Test
    void getByCode_shouldReturnToken_whenExists() {
        VerifToken vt = new VerifToken();
        vt.setCode("123456");

        when(verifTokenRepository.findByCode("123456"))
                .thenReturn(Optional.of(vt));

        VerifToken result = verifTokenService.getByCode("123456");

        assertThat(result).isEqualTo(vt);
    }

    @Test
    void getByCode_shouldThrow_whenNotFound() {
        when(verifTokenRepository.findByCode("123456"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> verifTokenService.getByCode("123456"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ---------------------------------------------------------------------
    // cleanExpiredVerifTokens()
    // ---------------------------------------------------------------------

    @Test
    void cleanExpiredVerifTokens_shouldCallRepositoryMethods() {
        verifTokenService.cleanExpiredVerifTokens();

        verify(verifTokenRepository).deleteAllByActivationNotNullAndUsed(true);
        verify(verifTokenRepository).deleteAllByExpirationBefore(any());
    }
}

