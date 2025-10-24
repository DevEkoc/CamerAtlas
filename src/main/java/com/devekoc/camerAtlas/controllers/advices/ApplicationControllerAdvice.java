package com.devekoc.camerAtlas.controllers.advices;

import com.devekoc.camerAtlas.dto.ErrorEntity;
import com.devekoc.camerAtlas.exceptions.PositionAlreadyFilledException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ApplicationControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class})
    public @ResponseBody ErrorEntity handleEntityNotFound (EntityNotFoundException ex) {
        return new ErrorEntity("404", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public @ResponseBody ErrorEntity handleIllegalArgumentException (IllegalArgumentException ex) {
        return new ErrorEntity("400", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public @ResponseBody ErrorEntity handleConflict(DataIntegrityViolationException ex) {
        return new ErrorEntity("409", "Contrainte d’intégrité violée : " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @ResponseBody ErrorEntity handleValidation(MethodArgumentNotValidException ex) {

        // Collecter toutes les erreurs (champs + globales)
        List<String> errors = new ArrayList<>();

        // Erreurs de champs
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.add(err.getField() + " : " + err.getDefaultMessage())
        );

        // Erreurs globales (comme @ValidDateRange)
        ex.getBindingResult().getGlobalErrors().forEach(err ->
                errors.add(err.getDefaultMessage())
        );

        String message = errors.isEmpty() ? "Requête invalide" : String.join(", ", errors);

        return new ErrorEntity("400", message);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PositionAlreadyFilledException.class)
    public @ResponseBody ErrorEntity handlePositionAlreadyFilledException(PositionAlreadyFilledException ex) {
        return new ErrorEntity("409", "Contrainte d’intégrité violée : " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public @ResponseBody ErrorEntity handleGeneric(Exception ex) {
        return new ErrorEntity("500", "Erreur interne : " + ex.getMessage());
    }
}