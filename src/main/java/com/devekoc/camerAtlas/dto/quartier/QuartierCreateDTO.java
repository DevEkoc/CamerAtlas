package com.devekoc.camerAtlas.dto.quartier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuartierCreateDTO(
        @NotBlank(message = "Le nom ne doit pas être vide !")
        @Size(min = 1, max = 50, message = "Le nom doit contenir entre 1 et 50 caractères")
        String nom,

        @NotBlank(message = "Le nom populaire ne doit pas être vide !")
        String nomPopulaire,

        @NotNull(message = "L'ID de la Sous-préfecture ne doit pas être vide !")
        Integer idSousPrefecture
) {
}
