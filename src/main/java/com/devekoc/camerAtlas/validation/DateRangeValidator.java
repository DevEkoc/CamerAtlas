package com.devekoc.camerAtlas.validation;

import com.devekoc.camerAtlas.dto.affectation.AffectationCreateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, AffectationCreateDTO> {

    @Override
    public boolean isValid(AffectationCreateDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.dateFin() == null || dto.dateDebut() == null) {
            return true; // null values are handled by @NotNull
        }
        return dto.dateFin().isAfter(dto.dateDebut());
    }
}