package com.devekoc.camerAtlas.suggestions;

import com.devekoc.camerAtlas.dto.subDivision.SubDivisionCreateDTO;
import com.devekoc.camerAtlas.enumerations.TargetType;
import com.devekoc.camerAtlas.services.SubDivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SubDivisionSuggestionHandler implements SuggestionHandler<SubDivisionCreateDTO> {

    private final SubDivisionService subDivisionService;

    @Override
    public void create(SubDivisionCreateDTO dto) {
        try {
            subDivisionService.create(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int id, SubDivisionCreateDTO dto) {
        try  {
            subDivisionService.update(id, dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        subDivisionService.delete(id);
    }

    @Override
    public Class<SubDivisionCreateDTO> dtoType() {
        return SubDivisionCreateDTO.class;
    }

    @Override
    public TargetType handledType() {
        return TargetType.SUBDIVISION;
    }
}

