package com.devekoc.camerAtlas.dto.user;

import com.devekoc.camerAtlas.validation.ValidCustomEmail;

public record PasswordResetDTO(
        @ValidCustomEmail
        String email,

        String code,

        String newPassword
) {
}
