package com.devekoc.camerAtlas.dto.division;


import com.devekoc.camerAtlas.dto.api.AuthorityDetailsDTO;
import com.devekoc.camerAtlas.dto.api.DelimitationDetailsDTO;

import java.util.List;

public record DivisionListDTO(
        Integer id,
        String name,
        Integer surface,
        Integer population,
        String gpsCoordinates,
        String divisionalOffice,
        Integer regionId,
        String regionName,
        String imageUrl,
        AuthorityDetailsDTO seniorDivisionalOfficer,
        List<DelimitationDetailsDTO> boundaries
) {

}

