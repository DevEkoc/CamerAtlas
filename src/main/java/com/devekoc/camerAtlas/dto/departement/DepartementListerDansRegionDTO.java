package com.devekoc.camerAtlas.dto.departement;

public record DepartementListerDansRegionDTO (
        Integer id,
        String nom,
        Integer superficie,
        Integer population,
        String coordonnees,
        String prefecture
) {
    
}
