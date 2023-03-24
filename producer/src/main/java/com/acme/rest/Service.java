package com.acme.rest;

import com.acme.kafka.ConfirmationModel;
import com.acme.kafka.OrderModel;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class Service {
    private final Validator validator;

    void order(@Valid final OrderModel order) {
        final var violations = validator.validate(order);
        if (!violations.isEmpty()) {
            log.debug("order: violations={}", violations);
            throw new OrderConstraintViolationsException(violations);
        }
    }

    void confirm(@Valid final ConfirmationModel confirmation) {
        final var violations = validator.validate(confirmation);
        if (!violations.isEmpty()) {
            log.debug("confirm: violations={}", violations);
            throw new ConfirmationConstraintViolationsException(violations);
        }
    }
}
