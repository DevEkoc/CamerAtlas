package com.devekoc.camerAtlas.security;

import com.devekoc.camerAtlas.services.JwtService;
import com.devekoc.camerAtlas.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {
    UserService userService;
    JwtService jwtService;

    public JwtFilter(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token;
        String username;
        final String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);

            try {
                // 1. Tente d'extraire le nom d'utilisateur.
                // Si la signature est mauvaise ou si le jeton est expiré, une exception est levée ici.
                username = jwtService.extractUserName(token);

                // 2. Si l'extraction réussit, le jeton est valide et non expiré.
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    // 3. Mise en place de l'authentification
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (ExpiredJwtException e) {
                // Gère l'expiration
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.getWriter().write("Authentification échouée: Jeton expiré");
                return; // 🔑 Bloque le traitement
            } catch (RuntimeException e) {
                // Intercepte les exceptions d'expiration/invalidité et rejette la requête.
                // On écrit directement dans la réponse pour indiquer l'échec d'authentification.
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Code 401
                response.getWriter().write("Authentification échouée: " + e.getMessage());
                return; // BLOQUE le filtre et ne passe pas à filterChain.doFilter
            }

        }

        filterChain.doFilter(request, response);
    }
}
