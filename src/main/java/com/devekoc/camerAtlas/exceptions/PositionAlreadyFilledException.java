package com.devekoc.camerAtlas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PositionAlreadyFilledException extends RuntimeException {
    public PositionAlreadyFilledException(String message) {
        super(message);
    }
}
