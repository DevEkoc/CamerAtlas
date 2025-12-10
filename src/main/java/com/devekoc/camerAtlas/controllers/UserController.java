package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.user.*;
import com.devekoc.camerAtlas.entities.RefreshToken;
import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.services.JwtService;
import com.devekoc.camerAtlas.services.RefreshTokenService;
import com.devekoc.camerAtlas.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "auth")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping(path = "register", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserListDTO> register (@RequestBody @Valid UserCreateDTO dto){
        UserListDTO created = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(path = "activate", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> activate (@RequestBody @Valid UserActivateDTO dto){
        userService.activate(dto);
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "login", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> logIn (@RequestBody @Valid UserConnectionDTO dto){
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );
        if (authenticate.isAuthenticated()) return ResponseEntity.ok(jwtService.generate(dto.username()));
        return null;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "logout", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout (@RequestBody Map<String, String> request){
        String refreshTokenValue = request.get(JwtService.REFRESH_TOKEN);
        refreshTokenService.revokeToken(refreshTokenValue);

        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "refresh", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        String oldRefreshToken = request.get(JwtService.REFRESH_TOKEN);
        RefreshToken validatedToken = refreshTokenService.verifyExpiration(oldRefreshToken);

        User user = validatedToken.getUser();
        // Par souci de sécurité, on révoque l'ancien token
        refreshTokenService.revokeToken(oldRefreshToken);

        // 4. Génération d'un NOUVEAU Access Token et d'un NOUVEAU Refresh Token
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return ResponseEntity.ok(Map.of(
                JwtService.ACCESS_TOKEN, newAccessToken,
                JwtService.REFRESH_TOKEN, newRefreshToken
        ));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "password/request-reset", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> requestResetPassword(@RequestBody @Valid RequestPwdResetDTO dto){
        userService.requestResetPassword(dto);
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "password/reset", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetDTO dto){
        userService.resetPassword(dto);
        return ResponseEntity.ok().build();
    }
}
