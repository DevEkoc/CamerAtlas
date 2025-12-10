package com.devekoc.camerAtlas.dto.user;

import com.devekoc.camerAtlas.enumerations.Role;
import com.devekoc.camerAtlas.validation.ValidCustomEmail;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public record UserCreateDTO(
        @NotNull(message = "Le nom de l'utilisateur ne doit pas être vide")
        String name,

        @NotNull(message = "Le mot de passe ne doit pas être vide")
        String password,

        @NotNull(message = "L'adresse mail est obligatoire")
        @ValidCustomEmail
        String email,
        
        @Enumerated(EnumType.STRING)
        @NotNull(message = "Le rôle de l'utilisateur est obligatoire")
        Role role
) {
}
