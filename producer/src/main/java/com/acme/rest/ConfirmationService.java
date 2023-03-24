package com.acme.rest;

import com.acme.kafka.ConfirmationModel;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationService {
    private final Validator validator;

    void confirm(@Valid final ConfirmationModel confirmation) {
        final var violations = validator.validate(confirmation);
        if (!violations.isEmpty()) {
            log.debug("confirm: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }
    }
}
