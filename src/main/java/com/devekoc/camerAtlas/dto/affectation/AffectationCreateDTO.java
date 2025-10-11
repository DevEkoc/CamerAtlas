package com.devekoc.camerAtlas.dto.affectation;

import com.devekoc.camerAtlas.enumerations.Fonction;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AffectationCreateDTO(
        @NotNull(message = "L'ID de l'autorité est obligatoire")
        Integer idAutorite,

        @NotNull(message = "La circonscription est obligatoire")
        Integer idCirconscription,

        @Enumerated(EnumType.STRING)
        @NotNull(message = "La fonction est obligatoire")
        Fonction fonction,

        @NotNull(message = "La date de début est obligatoire !")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate dateDebut,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate dateFin
) {
}
