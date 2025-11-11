package com.devekoc.camerAtlas.dto.subDivision;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record SubDivisionCreateDTO(
        @NotBlank(message = "Le name ne doit pas être vide !")
        @Size(max = 50, message = "Le name doit contenir au maximum 50 caractères")
        String name,

        @NotNull(message = "La surface ne doit pas être vide !")
        @Positive(message = "La surface doit être positive")
        Integer surface,

        @NotNull(message = "La population ne doit pas être vide !")
        @Positive(message = "La population doit être positive")
        Integer population,

        String gpsCoordinates,

        @NotBlank(message = "La Sous-préfecture ne doit pas être vide !")
        String subDivisionalOffice,

        @NotNull(message = "L'ID du département ne doit pas être vide !")
        Integer divisionId,

        MultipartFile image
) {
}
