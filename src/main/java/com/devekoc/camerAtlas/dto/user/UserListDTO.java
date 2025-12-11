package com.devekoc.camerAtlas.dto.user;

import com.devekoc.camerAtlas.enumerations.Role;

import java.time.Instant;

public record UserListDTO(
        int id,
        String name,
        String surname,
        String email,
        String pseudo,
        boolean isActive,
        boolean isLocked,
        Role role,
        String authorities,
        Instant createdAt,
        Instant lastConnection
) {
}
