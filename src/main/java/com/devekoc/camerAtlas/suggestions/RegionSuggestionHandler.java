package com.devekoc.camerAtlas.suggestions;

import com.devekoc.camerAtlas.dto.region.RegionCreateDTO;
import com.devekoc.camerAtlas.enumerations.TargetType;
import com.devekoc.camerAtlas.services.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RegionSuggestionHandler implements SuggestionHandler<RegionCreateDTO> {

    private final RegionService regionService;

    @Override
    public void create(RegionCreateDTO dto) {
        try {
            regionService.create(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int id, RegionCreateDTO dto) {
        try {
            regionService.update(id, dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        regionService.delete(id);
    }

    @Override
    public Class<RegionCreateDTO> dtoType() {
        return RegionCreateDTO.class;
    }

    @Override
    public TargetType handledType() {
        return TargetType.REGION;
    }
}

