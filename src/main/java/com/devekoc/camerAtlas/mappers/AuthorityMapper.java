package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.authority.AuthorityCreateDTO;
import com.devekoc.camerAtlas.dto.authority.AuthorityListDTO;
import com.devekoc.camerAtlas.entities.Authority;

public class AuthorityMapper {

    public static Authority fromCreateDTO (AuthorityCreateDTO dto, Authority authority) {
        authority.setName(dto.name());
        authority.setSurname(dto.surname());
        authority.setDateOfBirth(dto.dateOfBirth());
        return authority;
    }

    public static AuthorityListDTO toListDTO(Authority authority) {
        return new AuthorityListDTO(
                authority.getId(),
                authority.getName(),
                authority.getSurname(),
                authority.getDateOfBirth()
        );
    }
}
