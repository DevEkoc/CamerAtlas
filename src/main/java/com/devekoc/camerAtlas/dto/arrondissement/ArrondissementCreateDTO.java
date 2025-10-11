package com.devekoc.camerAtlas.dto.arrondissement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ArrondissementCreateDTO(
        @NotBlank(message = "Le nom ne doit pas être vide !")
        @Size(min = 1, max = 50, message = "Le nom doit contenir entre 1 et 50 caractères")
        String nom,

        @NotNull(message = "La superficie ne doit pas être vide !")
        @Positive(message = "La superficie doit être positive")
        Integer superficie,

        @NotNull(message = "La population ne doit pas être vide !")
        @Positive(message = "La population doit être positive")
        Integer population,

        String coordonnees,

        @NotBlank(message = "La Sous-préfecture ne doit pas être vide !")
        String sousPrefecture,

        @NotBlank(message = "L'ID de la région ne doit pas être vide !")
        Integer idDepartement
) {
}
