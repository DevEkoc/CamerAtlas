package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodCreateDTO;
import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodListDTO;
import com.devekoc.camerAtlas.entities.SubDivision;
import com.devekoc.camerAtlas.entities.Neighborhood;

public class NeighborhoodMapper {

    public static Neighborhood fromCreateDTO (NeighborhoodCreateDTO dto, Neighborhood neighborhood, SubDivision subDivision) {
        neighborhood.setName(dto.name());
        neighborhood.setPopularName(dto.popularName());
        neighborhood.setSubDivisionalOffice(subDivision);
        return neighborhood;
    }

    public static NeighborhoodListDTO toListDTO(Neighborhood neighborhood) {
        return new NeighborhoodListDTO(
                neighborhood.getId(),
                neighborhood.getName(),
                neighborhood.getPopularName(),
                neighborhood.getSubDivisionalOffice().getId(),
                neighborhood.getSubDivisionalOffice().getName()
        );
    }
}
