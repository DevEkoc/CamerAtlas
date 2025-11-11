package com.devekoc.camerAtlas.dto.subDivision;

import com.devekoc.camerAtlas.dto.api.AuthorityDetailsDTO;
import com.devekoc.camerAtlas.dto.api.DelimitationDetailsDTO;

import java.util.List;

public record SubDivisionListDTO(
        Integer id,
        String name,
        Integer surface,
        Integer population,
        String gpsCoordinates,
        String subDivisionalOffice,
        Integer departmentId,
        String departmentName,
        String imgUrl,
        AuthorityDetailsDTO subDivisionalOfficer,
        List<DelimitationDetailsDTO> boundaries
) {
}
