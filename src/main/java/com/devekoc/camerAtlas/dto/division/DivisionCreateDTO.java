package com.devekoc.camerAtlas.dto.division;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record DivisionCreateDTO(
        @Size(max = 50, message = "Le name doit contenir entre 1 et 50 caractères")
        @NotBlank(message = "Le name ne doit pas être vide !")
        String name,

        @NotNull(message = "La surface ne doit pas être vide !")
        @Positive(message = "La surface doit être positive")
        Integer surface,

        @NotNull(message = "La population ne doit pas être vide !")
        @Positive(message = "La population doit être positive")
        Integer population,

        String gpsCoordinates,

        @NotBlank(message = "La préfecture ne doit pas être vide !")
        String divisionalOffice,

        @NotNull(message = "L'ID de la région ne doit pas être vide !")
        Integer regionId,

        MultipartFile image
) {
}
