package com.acme.rest;

import com.acme.kafka.ConfirmationModel;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {

    private final Collection<ConstraintViolation<ConfirmationModel>> violations;

    ConstraintViolationsException(
        @SuppressWarnings("ParameterHidesMemberVariable")
        final Collection<ConstraintViolation<ConfirmationModel>> violations
    ) {
        super("Constraints are violated");
        this.violations = violations;
    }
}
