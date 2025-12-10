package com.devekoc.camerAtlas.dto.subDivision;

import com.devekoc.camerAtlas.dto.api.AuthorityDetailsDTO;
import com.devekoc.camerAtlas.dto.api.DelimitationDetailsDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SubDivisionListDTO(
        @Schema(description = "Nom de l'Arrondissement")
        Integer id,
        String name,
        Integer surface,
        Integer population,
        String gpsCoordinates,
        String subDivisionalOffice,
        Integer divisionId,
        String divisionName,
        String imgUrl,
        AuthorityDetailsDTO subDivisionalOfficer,
        List<DelimitationDetailsDTO> boundaries
) {
}
