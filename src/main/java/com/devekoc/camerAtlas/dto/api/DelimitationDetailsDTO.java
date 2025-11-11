package com.devekoc.camerAtlas.dto.api;

import com.devekoc.camerAtlas.enumerations.BorderType;

public record DelimitationDetailsDTO(
        BorderType borderType,
        String borderName
) {
}
