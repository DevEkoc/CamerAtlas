package com.devekoc.camerAtlas.dto.departement;


public record DepartementListerDTO(
        Integer id,

        String nom,

        Integer superficie,

        Integer population,

        String coordonnees,

        String prefecture,

        Integer idRegion,

        String nomRegion
) {

}

