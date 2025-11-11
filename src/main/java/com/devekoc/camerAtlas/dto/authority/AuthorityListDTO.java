package com.devekoc.camerAtlas.dto.authority;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record AuthorityListDTO(
        Integer id,
        String name,
        String surname,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth
) {
}
