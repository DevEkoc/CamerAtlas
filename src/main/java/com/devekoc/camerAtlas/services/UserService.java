package com.devekoc.camerAtlas.services;

import com.devekoc.camerAtlas.dto.user.*;
import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.entities.VerifToken;
import com.devekoc.camerAtlas.enumerations.TokenType;
import com.devekoc.camerAtlas.exceptions.TokenExpiredException;
import com.devekoc.camerAtlas.mappers.UserMapper;
import com.devekoc.camerAtlas.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final VerifTokenService verifTokenService;

    public UserListDTO register(UserCreateDTO dto) {
        validateUniqueName(dto.name());
        validateUniquePseudo(dto.pseudo());
        if (!dto.email().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))
            throw new IllegalArgumentException("Adresse mail invalide");
        validateUniqueEmail(dto.email());

        User user = UserMapper.fromCreateDTO(dto, new User());
        user.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        User created = userRepository.save(user);

        verifTokenService.generateToken(created, TokenType.ACTIVATION);
        return UserMapper.toListDTO(created);
    }

    public void activate(UserActivateDTO dto) {
        VerifToken verifToken = verifTokenService.getByCode(dto.code());
        if (Instant.now().isAfter(verifToken.getExpiration()))
            throw new TokenExpiredException("Votre code de vérification a expiré, veuillez en demander un autre");
        User user = userRepository.findById(verifToken.getUser().getId()).orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        user.setActive(true);
        verifToken.setActivation(Instant.now());
        verifToken.setUsed(true);
        verifTokenService.saveToken(verifToken);
        userRepository.save(user);
    }

    public void requestResetPassword(RequestPwdResetDTO dto) {
        User user = (User) loadUserByUsername(dto.email());
        verifTokenService.generateToken(user, TokenType.RESET);
    }

    public void resetPassword(PasswordResetDTO dto) {
        User user = (User) loadUserByUsername(dto.email());
        final VerifToken token = verifTokenService.getByCode(dto.code());

        if (token.getUser().getEmail().equals(user.getEmail())) {
            user.setPassword(bCryptPasswordEncoder.encode(dto.newPassword()));
            token.setActivation(Instant.now());
            token.setUsed(true);
            userRepository.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("Identifiants incorrects")
        );
    }

    private void validateUniqueName(String name) {
        if (userRepository.existsByName(name)) {
            throw new DataIntegrityViolationException("Un utilisateur avec le nom '" + name + "' existe déjà !");
        }
    }

    private void validateUniquePseudo(String pseudo) {
        if (userRepository.existsByPseudo(pseudo)) {
            throw new DataIntegrityViolationException("Un utilisateur avec le pseudo '" + pseudo + "' existe déjà !");
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Un utilisateur avec l'email '" + email + "' existe déjà !");
        }
    }


    public UserListDTO mapToProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return UserMapper.toListDTO(user);
    }

    public void setLastConnection(User user, Instant now) {
        user.setLastConnection(now);
        userRepository.save(user);
    }
}