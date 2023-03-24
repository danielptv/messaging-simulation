package com.acme.rest;

import com.acme.kafka.ConfirmationModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.unprocessableEntity;

@RestController
@RequestMapping("/rest")
@RequiredArgsConstructor
@Slf4j
public class ConfirmationController {
    private final ConfirmationService service;

    @PostMapping(path = "/confirm", consumes = APPLICATION_JSON_VALUE)
    @SuppressWarnings({"TrailingComment", "unused"})
    ResponseEntity<Void> confirm(
            @RequestBody final ConfirmationModel confirmation
    ) {
        log.info("confirm: {}", confirmation);
        service.confirm(confirmation);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(ConstraintViolationsException.class)
    @SuppressWarnings("unused")
    ResponseEntity<ProblemDetail> handleConstraintViolations(
            final ConstraintViolationsException ex,
            final HttpServletRequest request
    ) {
        log.info("handleConstraintViolations: {}", ex.getMessage());

        final var violations = ex.getViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " +
                        violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName() + " " +
                        violation.getMessage())
                .toList();
        log.trace("handleConstraintViolations: {}", violations);
        final String detail;
        if (violations.isEmpty()) {
            detail = "N/A";
        } else {
            final var violationsStr = violations.toString();
            detail = violationsStr.substring(1, violationsStr.length() - 2);
        }

        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);

        return unprocessableEntity().body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @SuppressWarnings("unused")
    ResponseEntity<ProblemDetail> handleMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpServletRequest request
    ) {
        log.info("handleMessageNotReadable: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
        return badRequest().body(problemDetail);
    }
}
