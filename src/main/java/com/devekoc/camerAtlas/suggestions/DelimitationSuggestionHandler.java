package com.devekoc.camerAtlas.suggestions;

import com.devekoc.camerAtlas.dto.delimitation.DelimitationCreateDTO;
import com.devekoc.camerAtlas.enumerations.TargetType;
import com.devekoc.camerAtlas.services.DelimitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DelimitationSuggestionHandler implements SuggestionHandler<DelimitationCreateDTO> {

    private final DelimitationService delimitationService;

    @Override
    public void create(DelimitationCreateDTO dto) {
        delimitationService.create(dto);
    }

    @Override
    public void update(int id, DelimitationCreateDTO dto) {
//        delimitationService.update(id, dto);
    }

    @Override
    public void delete(int id) {
        delimitationService.delete(id);
    }

    @Override
    public Class<DelimitationCreateDTO> dtoType() {
        return DelimitationCreateDTO.class;
    }

    @Override
    public TargetType handledType() {
        return TargetType.DELIMITATION;
    }
}

