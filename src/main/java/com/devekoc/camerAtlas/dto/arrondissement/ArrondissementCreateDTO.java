package com.devekoc.camerAtlas.dto.arrondissement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ArrondissementCreateDTO(
        @NotBlank(message = "Le nom ne doit pas être vide !")
        @Size(max = 50, message = "Le nom doit contenir au maximum 50 caractères")
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

        @NotNull(message = "L'ID du département ne doit pas être vide !")
        Integer idDepartement
) {
}
