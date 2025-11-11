package com.devekoc.camerAtlas.dto.region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record RegionCreateDTO(
        @NotBlank(message = "Le name ne doit pas être vide !")
        @Size(min = 1, max = 50, message = "Le name doit contenir entre 1 et 50 caractères")
        String name,

        @NotNull(message = "La surface ne doit pas être vide !")
        @Positive(message = "La surface doit être positive")
        Integer surface,

        @NotNull(message = "La population ne doit pas être vide !")
        @Positive(message = "La population doit être positive")
        Integer population,

        String gpsCoordinates,

        @NotBlank(message = "Le Chef-lieu ne doit pas être vide !")
        String capital,

        @Size(min = 2, max = 2, message = "Le code minéralogique doit contenir exactement 2 caractères")
        String mineralogicalCode,

        MultipartFile image
) {
}
