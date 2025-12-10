package com.devekoc.camerAtlas.dto.region;

import com.devekoc.camerAtlas.dto.api.DelimitationDetailsDTO;
import com.devekoc.camerAtlas.dto.api.AuthorityDetailsDTO;

import java.util.List;

public record RegionListDTO(
        Integer id,
        String name,
        Integer surface,
        Integer population,
        String gpsCoordinates,
        String capital,
        String mineralogicalCode,
        String imgUrl,
        AuthorityDetailsDTO governor,
        List<DelimitationDetailsDTO> boundaries
) {
}
