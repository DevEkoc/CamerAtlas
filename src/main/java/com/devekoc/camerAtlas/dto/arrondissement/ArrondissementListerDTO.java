package com.devekoc.camerAtlas.dto.arrondissement;

import com.devekoc.camerAtlas.dto.departement.DepartementListerDansRegionDTO;

import java.util.List;

public record ArrondissementListerDTO(
        Integer id,
        String nom,
        Integer superficie,
        Integer population,
        String coordonnees,
        String sousPrefecture,
        Integer idDepartement,
        String nomDepartement
) {
}
