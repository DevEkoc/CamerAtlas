package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.dto.delimitation.DelimitationListDTO;
import com.devekoc.camerAtlas.entities.Circonscription;
import com.devekoc.camerAtlas.entities.Delimitation;

public class DelimitationMapper {

    public static Delimitation fromCreateDTO (DelimitationCreateDTO dto, Delimitation delimitation, Circonscription circonscription) {
        delimitation.setCirconscription(circonscription);
        delimitation.setBorderType(dto.borderType());
        delimitation.setBorderName(dto.borderName());
        return delimitation;
    }

    public static DelimitationListDTO toListDTO(Delimitation delimitation) {
        return new DelimitationListDTO(
                delimitation.getId(),
                delimitation.getCirconscription().getId(),
                delimitation.getCirconscription().getName(),
                delimitation.getBorderType(),
                delimitation.getBorderName()
        );
    }
}
