package com.devekoc.camerAtlas.suggestions;

import com.devekoc.camerAtlas.dto.authority.AuthorityCreateDTO;
import com.devekoc.camerAtlas.enumerations.TargetType;
import com.devekoc.camerAtlas.services.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthoritySuggestionHandler implements SuggestionHandler<AuthorityCreateDTO> {

    private final AuthorityService authorityService;

    @Override
    public void create(AuthorityCreateDTO dto) {
        authorityService.create(dto);
    }

    @Override
    public void update(int id, AuthorityCreateDTO dto) {
        authorityService.update(id, dto);
    }

    @Override
    public void delete(int id) {
        authorityService.delete(id);
    }

    @Override
    public Class<AuthorityCreateDTO> dtoType() {
        return AuthorityCreateDTO.class;
    }

    @Override
    public TargetType handledType() {
        return TargetType.AUTHORITY;
    }
}

