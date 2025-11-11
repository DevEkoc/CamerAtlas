package com.devekoc.camerAtlas.validation;

import com.devekoc.camerAtlas.dto.appointment.AppointmentCreateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, AppointmentCreateDTO> {

    @Override
    public boolean isValid(AppointmentCreateDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.endDate() == null || dto.startDate() == null) {
            return true; // null values are handled by @NotNull
        }
        return dto.endDate().isAfter(dto.startDate());
    }
}