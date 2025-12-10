package com.devekoc.camerAtlas.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomEmailValidator.class)
@Documented
public @interface ValidCustomEmail {
    String message() default "L'adresse email n'est pas au format requis.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
