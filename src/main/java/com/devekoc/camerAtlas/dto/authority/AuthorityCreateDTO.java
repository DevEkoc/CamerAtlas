package com.devekoc.camerAtlas.dto.authority;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AuthorityCreateDTO(
        @NotBlank(message = "Le name ne peut pas être vide !")
        @Size(max = 50, message = "Le name doit faire maximum 50 caractères")
        String name,

        @Size(max = 50, message = "Le prénom doit faire maximum 50 caractères")
        String surname,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth
) {
}
