package com.devekoc.camerAtlas.dto.affectation;

import com.devekoc.camerAtlas.enumerations.Fonction;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AffectationListerDTO(
        Integer idAffectation,
        Integer IdAutorite,
        Integer idCirconscription,
        Fonction fonction,
        LocalDate dateDebut,
        LocalDate dateFin
) {
}
