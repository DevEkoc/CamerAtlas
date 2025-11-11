package com.devekoc.camerAtlas.dto.neighborhood;

public record NeighborhoodListDTO(
        Integer id,
        String name,
        String popularName,
        Integer subDivisionalOfficeId,
        String subDivisionalOfficeName
) {
}
