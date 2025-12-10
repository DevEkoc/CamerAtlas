package com.devekoc.camerAtlas.mappers;

import com.devekoc.camerAtlas.dto.user.UserCreateDTO;
import com.devekoc.camerAtlas.dto.user.UserListDTO;
import com.devekoc.camerAtlas.entities.User;

public class UserMapper {

    public static User fromCreateDTO(UserCreateDTO dto, User user) {
        user.setName(dto.name());
        user.setPassword(dto.password());
        user.setEmail(dto.email().toLowerCase());
        user.setRole(dto.role());
        return user;
    }

    public static UserListDTO toListDTO(User user) {
        return new UserListDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isActive(),
                user.isLocked(),
                user.getRole()
        );
    }
}
