package com.devekoc.camerAtlas.dto.region;

import com.devekoc.camerAtlas.dto.api.DelimitationDetailsDTO;
import com.devekoc.camerAtlas.dto.api.AuthorityDetailsDTO;
import com.devekoc.camerAtlas.dto.division.DivisionListDTO;

import java.util.List;

public record RegionListWithDivisionsDTO(
        Integer id,
        String name,
        Integer surface,
        Integer population,
        String gpsCoordinates,
        String capital,
        String mineralogicalCode,
        String imgUrl,
        AuthorityDetailsDTO governor,
        List<DelimitationDetailsDTO> boundaries,
        List<DivisionListDTO>divisions
) {
}
