package com.devekoc.camerAtlas.dto.quartier;

public record QuartierListerDTO(
        String nom,
        String nomPopulaire,
        Integer idSousPrefecture,
        String nomSousPrefecture
) {
}
