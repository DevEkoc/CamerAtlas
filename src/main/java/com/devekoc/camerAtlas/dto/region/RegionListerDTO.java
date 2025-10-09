package com.devekoc.camerAtlas.dto.region;

import com.devekoc.camerAtlas.dto.departement.DepartementListerDansRegionDTO;

import java.util.List;

public record RegionListerDTO(
        String nom,
        Integer superficie,
        Integer population,
        String coordonnees,
        String chefLieu,
        String codeMineralogique,
        List<DepartementListerDansRegionDTO> departements
) {
}
