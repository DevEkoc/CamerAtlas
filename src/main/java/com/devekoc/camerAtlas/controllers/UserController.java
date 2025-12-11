package com.devekoc.camerAtlas.controllers;

import com.devekoc.camerAtlas.dto.user.*;
import com.devekoc.camerAtlas.entities.RefreshToken;
import com.devekoc.camerAtlas.entities.User;
import com.devekoc.camerAtlas.services.JwtService;
import com.devekoc.camerAtlas.services.RefreshTokenService;
import com.devekoc.camerAtlas.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Utilisateurs & Authentification", description = "Gestion des utilisateurs et du processus d'authentification (inscription, connexion, rafraîchissement de token, réinitialisation de mot de passe)")
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "auth")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "Enregistre un nouvel utilisateur",
            description = "Inscrit un nouvel utilisateur avec un nom d'utilisateur, une adresse e-mail et un mot de passe. " +
                    "Un e-mail de vérification est envoyé pour activer le compte."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Utilisateur enregistré avec succès (e-mail de vérification envoyé)"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée ou adresse e-mail invalide)"),
                    @ApiResponse(responseCode = "409", description = "Un utilisateur avec le même nom ou la même adresse e-mail existe déjà")
            }
    )
    @PostMapping(path = "register", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserListDTO> register (@RequestBody @Valid UserCreateDTO dto){
        UserListDTO created = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Active un compte utilisateur",
            description = "Active un compte utilisateur en utilisant le code de vérification reçu par e-mail."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Compte activé avec succès"),
                    @ApiResponse(responseCode = "400", description = "Code de vérification expiré ou invalide"),
                    @ApiResponse(responseCode = "404", description = "Code de vérification ou utilisateur introuvable")
            }
    )
    @PostMapping(path = "activate", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> activate (@RequestBody @Valid UserActivateDTO dto){
        userService.activate(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Connecte un utilisateur",
            description = "Authentifie un utilisateur et génère un jeton d'accès (access token) et un jeton de rafraîchissement (refresh token)."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Connexion réussie, retourne les jetons"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
                    @ApiResponse(responseCode = "401", description = "Identifiants incorrects ou compte non activé"),
                    @ApiResponse(responseCode = "403", description = "Compte désactivé ou verrouillé")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "login", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> logIn (@RequestBody @Valid UserConnectionDTO dto, HttpServletResponse response){
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );
        if (authenticate.isAuthenticated()) { // Test redondant, mais utile pour la clarté
            Map<String, String> tokens = jwtService.generate(dto.username());

            String refreshToken = tokens.get("refresh_token");
            response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(refreshToken).toString());

            return ResponseEntity.ok(Map.of("access_token", tokens.get("access_token")));
        }
        return null; // Should not be reached as AuthenticationManager throws exceptions
    }

    @Operation(
            summary = "Déconnecte un utilisateur",
            description = "Invalide le jeton de rafraîchissement fourni, déconnectant ainsi l'utilisateur."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
                    @ApiResponse(responseCode = "400", description = "Requête invalide (jeton de rafraîchissement manquant)"),
                    @ApiResponse(responseCode = "401", description = "Jeton de rafraîchissement invalide ou non authentifié")
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(path = "logout")
    public ResponseEntity<Void> logout (@CookieValue(value = "refresh_token", required = false) String cookieToken, HttpServletResponse response){
        // Invalidation de l'ancien jeton en BD. La valeur est récupérée depuis le cookie.
        if (cookieToken != null && !cookieToken.isEmpty()) {
            refreshTokenService.revokeToken(cookieToken);
        }
        // Suppression côté client (par écrasement)
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Rafraîchit les jetons d'authentification",
            description = "Utilise un jeton de rafraîchissement pour obtenir un nouveau jeton d'accès et un nouveau jeton de rafraîchissement."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Nouveaux jetons générés avec succès"),
                    @ApiResponse(responseCode = "400", description = "Requête invalide (jeton de rafraîchissement manquant)"),
                    @ApiResponse(responseCode = "401", description = "Jeton de rafraîchissement invalide ou expiré")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "refresh", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> refresh(@CookieValue(value = "refresh_token", required = false) String cookieToken, HttpServletResponse response) {
        if (cookieToken == null || cookieToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        RefreshToken validatedToken = refreshTokenService.verifyExpiration(cookieToken);

        User user = validatedToken.getUser();
        // Par souci de sécurité, on révoque l'ancien token
        refreshTokenService.revokeToken(cookieToken);

        // 4. Génération d'un NOUVEAU Access Token et d'un NOUVEAU Refresh Token
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(user).getToken();

        // 5. Écriture du nouveau Refresh Token dans le cookie
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(newRefreshToken).toString());

        return ResponseEntity.ok(Map.of(JwtService.ACCESS_TOKEN, newAccessToken));
    }

    @Operation(
            summary = "Demande une réinitialisation de mot de passe",
            description = "Envoie un e-mail avec un code de vérification pour réinitialiser le mot de passe de l'utilisateur. " +
                    "Pour des raisons de sécurité, une réponse 200 est toujours renvoyée, même si l'e-mail n'existe pas."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "E-mail de réinitialisation envoyé (si l'adresse existe)"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée)"),
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "password/request-reset", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> requestResetPassword(@RequestBody @Valid RequestPwdResetDTO dto){
        userService.requestResetPassword(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Réinitialise le mot de passe",
            description = "Réinitialise le mot de passe d'un utilisateur en utilisant un code de vérification et un nouveau mot de passe."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides (validation échouée, code de vérification expiré ou incorrect, ou code non correspondant à l'utilisateur)"),
                    @ApiResponse(responseCode = "404", description = "Utilisateur ou code de vérification introuvable")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "password/reset", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetDTO dto){
        userService.resetPassword(dto);
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "me")
    public ResponseEntity<UserListDTO> me(Authentication authentication){
        return ResponseEntity.ok(userService.mapToProfile(authentication));
    }

    private ResponseCookie buildCookie (String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // doit être true en production
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax") // doit être Strict pour la prod
                .build();
    }
}
