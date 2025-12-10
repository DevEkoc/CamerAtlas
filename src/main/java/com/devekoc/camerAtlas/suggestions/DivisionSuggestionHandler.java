package com.devekoc.camerAtlas.suggestions;

import com.devekoc.camerAtlas.dto.division.DivisionCreateDTO;
import com.devekoc.camerAtlas.enumerations.TargetType;
import com.devekoc.camerAtlas.services.DivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DivisionSuggestionHandler implements SuggestionHandler<DivisionCreateDTO> {

    private final DivisionService divisionService;

    @Override
    public void create(DivisionCreateDTO dto) {
        try {
            divisionService.create(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int id, DivisionCreateDTO dto) {
        try {
            divisionService.update(id, dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        divisionService.delete(id);
    }

    @Override
    public Class<DivisionCreateDTO> dtoType() {
        return DivisionCreateDTO.class;
    }

    @Override
    public TargetType handledType() {
        return TargetType.DIVISION;
    }
}

