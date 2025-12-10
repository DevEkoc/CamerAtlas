package com.devekoc.camerAtlas.dto.user;

import com.devekoc.camerAtlas.validation.ValidCustomEmail;

public record RequestPwdResetDTO(@ValidCustomEmail String email) {
}
