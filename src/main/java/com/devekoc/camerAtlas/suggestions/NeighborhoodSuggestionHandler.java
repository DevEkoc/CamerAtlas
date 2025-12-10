package com.devekoc.camerAtlas.suggestions;

import com.devekoc.camerAtlas.dto.neighborhood.NeighborhoodCreateDTO;
import com.devekoc.camerAtlas.enumerations.TargetType;
import com.devekoc.camerAtlas.services.NeighborhoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NeighborhoodSuggestionHandler implements SuggestionHandler<NeighborhoodCreateDTO> {

    private final NeighborhoodService neighborhoodService;

    @Override
    public void create(NeighborhoodCreateDTO dto) {
        neighborhoodService.create(dto);
    }

    @Override
    public void update(int id, NeighborhoodCreateDTO dto) {
        neighborhoodService.update(id, dto);
    }

    @Override
    public void delete(int id) {
        neighborhoodService.delete(id);
    }

    @Override
    public Class<NeighborhoodCreateDTO> dtoType() {
        return NeighborhoodCreateDTO.class;
    }

    @Override
    public TargetType handledType() {
        return TargetType.NEIGHBORHOOD;
    }
}

