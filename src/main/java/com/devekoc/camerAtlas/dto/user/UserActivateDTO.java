package com.devekoc.camerAtlas.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserActivateDTO(
        @NotNull(message = "Le code d'activation est requis")
        @Size(min = 6, max = 6, message = "Le code attendu doit contenir 6 caractères")
        String code
) {
}
