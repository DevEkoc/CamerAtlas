package com.devekoc.camerAtlas.dto.quartier;

public record QuartierListerDTO(
        Integer id,
        String nom,
        String nomPopulaire,
        Integer idSousPrefecture,
        String nomSousPrefecture
) {
}
