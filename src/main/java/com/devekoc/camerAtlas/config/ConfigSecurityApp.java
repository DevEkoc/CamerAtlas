package com.devekoc.camerAtlas.config;

import com.devekoc.camerAtlas.security.JwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class ConfigSecurityApp {
    private final JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception {
        List<String> authorizedUrls = List.of(
                "/auth/register",
                "/auth/activate",
                "/auth/login",
                "/auth/password/**",
                "/auth/refresh"
        );

        List<String> adminUrls = List.of(
                "/regions/**",
                "/divisions/**",
                "/subDivisions/**",
                "/neighborhoods/**",
                "/authorities/**",
                "/appointments/**",
                "/delimitations/**"
        );

        return httpSecurity
                .csrf(AbstractHttpConfigurer:: disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html"
                                ).permitAll()

                                // Authentification
                                .requestMatchers(POST, authorizedUrls.toArray(new String[0])).permitAll()
                                // Logout doit être authentifié (pour éviter les abus)
                                .requestMatchers(POST, "/auth/logout").authenticated()

                                // Médias
                                .requestMatchers(GET, "/media/**").permitAll()

                                // Lecture des entités métier (tout le monde peut consulter)
                                .requestMatchers(GET, adminUrls.toArray(new String[0])).permitAll()

                                // Création, modification et suppression des entités métier (ADMIN uniquement)
                                .requestMatchers(POST, adminUrls.toArray(new String[0])).hasRole("ADMIN")
                                .requestMatchers(PUT, adminUrls.toArray(new String[0])).hasRole("ADMIN")
                                .requestMatchers(DELETE, adminUrls.toArray(new String[0])).hasRole("ADMIN")

                                // Suggestions
                                .requestMatchers(POST, "/suggestions").hasAuthority("SUGGESTION_CREATE")
                                .requestMatchers(GET,  "/suggestions/**").hasAuthority("SUGGESTION_READ")

                                .requestMatchers(POST, "/suggestions/*/approve").hasAuthority("SUGGESTION_APPROVE")
                                .requestMatchers(POST, "/suggestions/*/reject") .hasAuthority("SUGGESTION_REJECT")

                                        .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                )
                .cors(Customizer.withDefaults())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
