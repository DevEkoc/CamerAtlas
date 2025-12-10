package com.devekoc.camerAtlas.dto.subDivision;

import com.devekoc.camerAtlas.dto.api.AuthorityDetailsDTO;
import com.devekoc.camerAtlas.dto.api.DelimitationDetailsDTO;
import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodListDTO;

import java.util.List;

public record SubDivisionWithNeighborhoodsDTO(
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
        List<DelimitationDetailsDTO> boundaries,
        List<NeighborhoodListDTO> neighborhoods
) {
}
