package com.devekoc.camerAtlas.dto.neighborhood;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NeighborhoodCreateDTO(
        @NotBlank(message = "Le name ne doit pas être vide !")
        @Size(max = 50, message = "Le name doit contenir entre 1 et 50 caractères")
        String name,

        @NotBlank(message = "Le name populaire ne doit pas être vide !")
        String popularName,

        @NotNull(message = "L'ID de la Sous-préfecture ne doit pas être vide !")
        Integer subDivisionalOfficeId
) {
}
