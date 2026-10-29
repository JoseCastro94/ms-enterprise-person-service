package com.enterprise.app.business.validators;

import com.enterprise.app.model.enums.Genero;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GeneroValidoValidator implements ConstraintValidator<GeneroValido, Genero> {
    @Override
    public boolean isValid(Genero genero, ConstraintValidatorContext context) {
        return genero == null || genero != Genero.UNSUPPORTED;
    }
}
