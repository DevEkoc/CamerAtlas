package com.devekoc.camerAtlas.services;


import com.devekoc.camerAtlas.dto.user.*;
import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.entities.VerifToken;
import com.devekoc.camerAtlas.enumerations.Role;
import com.devekoc.camerAtlas.enumerations.TokenType;
import com.devekoc.camerAtlas.exceptions.TokenExpiredException;
import com.devekoc.camerAtlas.repositories.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder passwordEncoder;
    @Mock VerifTokenService verifTokenService;

    @InjectMocks UserService userService;

    // --------------------------------------------------------------------
    //  register()
    // --------------------------------------------------------------------

    @Test
    void register_shouldCreateUserAndSendActivationToken() {
        UserCreateDTO dto = new UserCreateDTO(
                "Doe",
                "John",
                "JohnnyDoe",
                "john@gmail.com",
                "pass123",
                Role.ADMIN
        );

        when(userRepository.existsByName("Doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);

        User saved = new User();
        saved.setId(1);
        saved.setName("Doe");
        saved.setSurname("John");
        saved.setPseudo("JohnnyDoe");
        saved.setEmail("john@gmail.com");
        saved.setRole(Role.ADMIN);

        when(passwordEncoder.encode("pass123")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserListDTO result = userService.register(dto);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Doe");

        verify(verifTokenService).generateToken(saved, TokenType.ACTIVATION);
    }

    @Test
    void register_shouldThrowWhenEmailInvalid() {
        UserCreateDTO dto = new UserCreateDTO(
                "Doe",
                "John",
                "JohnnyDoe",
                "badEmail",
                "pass123",
                Role.CONTRIBUTOR
        );

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Adresse mail invalide");
    }

    @Test
    void register_shouldThrowWhenNameExists() {
        UserCreateDTO dto = new UserCreateDTO(
                "John", "pass", "Johnny", "john@mail.fr", "pass", Role.CONTRIBUTOR
        );

        when(userRepository.existsByName("John")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void register_shouldThrowWhenEmailExists() {
        UserCreateDTO dto = new UserCreateDTO(
                "John", "pass", "Johnny", "john@mail.fr", "pass", Role.CONTRIBUTOR
        );

        when(userRepository.existsByName("John")).thenReturn(false);
        when(userRepository.existsByEmail("john@mail.fr")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // --------------------------------------------------------------------
    // activate()
    // --------------------------------------------------------------------

    @Test
    void activate_shouldEnableUserAndMarkTokenUsed() {
        User user = new User();
        user.setId(1);
        user.setEmail("john@mail.com");
        user.setName("John");

        VerifToken token = new VerifToken();
        token.setUser(user);
        token.setExpiration(Instant.now().plusSeconds(600));

        when(verifTokenService.getByCode("123456")).thenReturn(token);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserActivateDTO dto = new UserActivateDTO("123456");

        userService.activate(dto);

        assertThat(user.isActive()).isTrue();
        assertThat(token.isUsed()).isTrue();
        assertThat(token.getActivation()).isNotNull();

        verify(verifTokenService).saveToken(token);
        verify(userRepository).save(user);
    }

    @Test
    void activate_shouldThrowWhenTokenExpired() {
        VerifToken token = new VerifToken();
        token.setExpiration(Instant.now().minusSeconds(1)); // expiré

        when(verifTokenService.getByCode("123456")).thenReturn(token);

        UserActivateDTO dto = new UserActivateDTO("123456");

        assertThatThrownBy(() -> userService.activate(dto))
                .isInstanceOf(TokenExpiredException.class);
    }

    // --------------------------------------------------------------------
    // requestResetPassword()
    // --------------------------------------------------------------------

    @Test
    void requestResetPassword_shouldGenerateResetToken() {
        User user = new User();
        user.setEmail("john@mail.com");

        when(userRepository.findByEmail("john@mail.com"))
                .thenReturn(Optional.of(user));

        RequestPwdResetDTO dto = new RequestPwdResetDTO("john@mail.com");

        userService.requestResetPassword(dto);

        verify(verifTokenService)
                .generateToken(user, TokenType.RESET);
    }

    // --------------------------------------------------------------------
    // resetPassword()
    // --------------------------------------------------------------------

    @Test
    void resetPassword_shouldUpdatePasswordAndMarkTokenUsed() {
        User user = new User();
        user.setEmail("john@mail.com");

        VerifToken token = new VerifToken();
        token.setUser(user);

        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));
        when(verifTokenService.getByCode("999999")).thenReturn(token);
        when(passwordEncoder.encode("newPass")).thenReturn("ENCODED");

        PasswordResetDTO dto = new PasswordResetDTO(
                "john@mail.com",
                "999999",
                "newPass"
        );

        userService.resetPassword(dto);

        assertThat(user.getPassword()).isEqualTo("ENCODED");
        assertThat(token.isUsed()).isTrue();
        assertThat(token.getActivation()).isNotNull();

        verify(userRepository).save(user);
    }

    // --------------------------------------------------------------------
    // loadUserByUsername()
    // --------------------------------------------------------------------

    @Test
    void loadUserByUsername_shouldReturnUser() {
        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("test@mail.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void loadUserByUsername_shouldThrowWhenNotFound() {
        when(userRepository.findByEmail("notfound@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("notfound@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}

