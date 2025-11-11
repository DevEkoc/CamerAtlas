package com.devekoc.camerAtlas.dto.api;

import java.time.LocalDate;

public record AuthorityDetailsDTO(
        String name,
        String surname,
        LocalDate dateOfBirth,
        String fonction,
        LocalDate startDate,
        LocalDate endDate
) {
}
