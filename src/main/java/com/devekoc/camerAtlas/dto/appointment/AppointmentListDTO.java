package com.devekoc.camerAtlas.dto.appointment;

import com.devekoc.camerAtlas.enumerations.Fonction;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

public record AppointmentListDTO(
        Integer appointmentId,
        Integer authorityId,
        Integer circonscriptionId,
        @Enumerated(EnumType.STRING)
        Fonction fonction,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate startDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate endDate
) {
}
