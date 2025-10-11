package com.devekoc.camerAtlas.dto.delimitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DelimitationCreateDTO(
        @NotNull(message = "Le code de la circonscription ne doit pas être vide")
        Integer codeCirconscription,

        @NotNull(message = "L'ID de la frontière ne doit pas vide")
        Integer idFrontiere,

        @NotBlank(message = "Le nom de la circonscription ne doit pas être null")
        String nomCirconscription,

        @NotBlank(message = "Le type de la frontière ne doit pas être")
        String typeFrontiere
) {
}
