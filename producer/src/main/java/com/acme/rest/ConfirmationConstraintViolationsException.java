package com.acme.rest;

import com.acme.kafka.ConfirmationModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
@SuppressFBWarnings("EI_EXPOSE_REP")
public class ConfirmationConstraintViolationsException extends RuntimeException {

    private final Collection<ConstraintViolation<ConfirmationModel>> violations;

    ConfirmationConstraintViolationsException(
        @SuppressWarnings("ParameterHidesMemberVariable")
        final Collection<ConstraintViolation<ConfirmationModel>> violations
    ) {
        super("Constraints are violated");
        this.violations = violations;
    }
}
