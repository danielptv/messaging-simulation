package com.acme.rest;

import com.acme.kafka.OrderModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
@SuppressFBWarnings("EI_EXPOSE_REP")
public class OrderConstraintViolationsException extends RuntimeException {

    private final Collection<ConstraintViolation<OrderModel>> violations;

    OrderConstraintViolationsException(
        @SuppressWarnings("ParameterHidesMemberVariable")
        final Collection<ConstraintViolation<OrderModel>> violations
    ) {
        super("Constraints are violated");
        this.violations = violations;
    }
}
