package com.devekoc.camerAtlas.dto.delimitation;

import com.devekoc.camerAtlas.enumerations.BorderType;

public record DelimitationListDTO(
        Integer delimitationId,
        Integer circonscriptionId,
        String circonscriptionName,
        BorderType borderType,
        String borderName
) {
}
