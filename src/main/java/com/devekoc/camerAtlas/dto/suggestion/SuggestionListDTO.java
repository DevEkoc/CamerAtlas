package com.devekoc.camerAtlas.dto.suggestion;

import com.devekoc.camerAtlas.enumerations.SuggestionStatus;
import com.devekoc.camerAtlas.enumerations.SuggestionType;
import com.devekoc.camerAtlas.enumerations.TargetType;

import java.time.Instant;

public record SuggestionListDTO(
        int id,
        SuggestionType type,
        TargetType targetType,
        int targetId,
        String payload,
        SuggestionStatus status,
        Instant submittedAt,
        String submittedBy // nom de l'utilisateur qui a soumis la requête
) {}
