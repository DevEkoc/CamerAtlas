package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.suggestion.SuggestionListDTO;
import com.devekoc.camerAtlas.entities.Suggestion;

public final class SuggestionMapper {

    private SuggestionMapper() {}

    public static SuggestionListDTO toDTO(Suggestion s) {
        return new SuggestionListDTO(
                s.getId(),
                s.getType(),
                s.getTargetType(),
                s.getTargetId(),
                s.getPayload(),
                s.getStatus(),
                s.getSubmittedAt(),
                s.getSubmittedBy().getUsername()
        );
    }
}

