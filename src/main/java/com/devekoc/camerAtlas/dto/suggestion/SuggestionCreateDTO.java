package com.devekoc.camerAtlas.dto.suggestion;

import com.devekoc.camerAtlas.enumerations.SuggestionType;
import com.devekoc.camerAtlas.enumerations.TargetType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SuggestionCreateDTO(
        @NotNull @Enumerated(EnumType.STRING) SuggestionType type,
        @NotNull @Enumerated(EnumType.STRING) TargetType targetType,
        int targetId, // id de l'entité concernée, null pour une suggestion de création
        @NotBlank String payload
) {
}
