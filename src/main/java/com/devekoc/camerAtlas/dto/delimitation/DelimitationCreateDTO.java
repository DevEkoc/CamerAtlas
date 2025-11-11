package com.devekoc.camerAtlas.dto.delimitation;

import com.devekoc.camerAtlas.enumerations.BorderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DelimitationCreateDTO(
        @NotNull(message = "Le code de la circonscription ne doit pas être vide")
        Integer circonscriptionId,

        @NotNull(message = "Le type de la frontière ne doit pas être vide")
        BorderType borderType,

        @NotBlank(message = "Le name de la frontière ne doit pas être vide")
        String borderName
) {
}
