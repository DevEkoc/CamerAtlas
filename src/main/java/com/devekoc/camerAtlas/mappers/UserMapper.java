package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.user.UserCreateDTO;
import com.devekoc.camerAtlas.dto.user.UserListDTO;
import com.devekoc.camerAtlas.entities.User;

import java.time.Instant;

public class UserMapper {

    public static User fromCreateDTO(UserCreateDTO dto, User user) {
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setPseudo(dto.pseudo());
        user.setPassword(dto.password());
        user.setEmail(dto.email().toLowerCase());
        user.setRole(dto.role());
        user.setCreatedAt(Instant.now());
        return user;
    }

    public static UserListDTO toListDTO(User user) {
        return new UserListDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPseudo(),
                user.isActive(),
                user.isLocked(),
                user.getRole(),
                user.getAuthorities().toString(),
                user.getCreatedAt(),
                user.getLastConnection()
        );
    }
}
