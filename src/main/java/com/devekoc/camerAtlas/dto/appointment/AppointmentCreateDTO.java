package com.devekoc.camerAtlas.dto.appointment;

import com.devekoc.camerAtlas.enumerations.Function;
import com.devekoc.camerAtlas.validation.ValidDateRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@ValidDateRange
public record AppointmentCreateDTO(
        @NotNull(message = "L'ID de l'autorité est obligatoire")
        Integer authorityId,

        @NotNull(message = "La circonscription est obligatoire")
        Integer circonscriptionId,

        @Enumerated(EnumType.STRING)
        @NotNull(message = "La function est obligatoire")
        Function function,

        @NotNull(message = "La date de début est obligatoire !")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate startDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate endDate
) {
}
