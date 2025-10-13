package com.devekoc.camerAtlas.dto.delimitation;

import com.devekoc.camerAtlas.enumerations.TypeFrontiere;

public record DelimitationListerDTO(
        Integer idDelimitation,
        Integer codeCirconscription,
        Integer idFrontiere,
        String nomCirconscription,
        TypeFrontiere typeFrontiere,
        String limiteFrontiere
) {
}
