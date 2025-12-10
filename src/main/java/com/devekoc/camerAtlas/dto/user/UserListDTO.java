package com.devekoc.camerAtlas.dto.user;

import com.devekoc.camerAtlas.enumerations.Role;

public record UserListDTO(
        int id,
        String name,
        String email,
        boolean isActive,
        boolean isLocked,
        Role role
) {
}
