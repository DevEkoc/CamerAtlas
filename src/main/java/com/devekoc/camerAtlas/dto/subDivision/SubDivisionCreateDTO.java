package com.devekoc.camerAtlas.dto.subDivision;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record SubDivisionCreateDTO(
        @Schema(description = "Nom de l'Arrondissement")
        @NotBlank(message = "Le nom ne doit pas être vide !")
        @Size(max = 50, message = "Le nom doit contenir au maximum 50 caractères")
        String name,


        @Schema(description = "Surface de l'Arrondissement")
        @NotNull(message = "La surface ne doit pas être vide !")
        @Positive(message = "La surface doit être positive")
        Integer surface,

        @Schema(description = "Population de l'Arrondissement")
        @NotNull(message = "La population ne doit pas être vide !")
        @Positive(message = "La population doit être positive")
        Integer population,

        @Schema(description = "Coordonnées GPS de l'Arrondissement")
        String gpsCoordinates,

        @Schema(description = "Nom du Chef-Lieu de l'Arrondissement")
        @NotBlank(message = "La Sous-préfecture ne doit pas être vide !")
        String subDivisionalOffice,

        @Schema(description = "ID du Département auquel appartient l'Arrondissement")
        @NotNull(message = "L'ID du département ne doit pas être vide !")
        Integer divisionId,

        @Schema(description = "Photo représentant l'Arrondissement")
        MultipartFile image
) {
}
