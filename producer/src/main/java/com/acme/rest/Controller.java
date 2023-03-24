package com.acme.rest;

import com.acme.kafka.ConfirmationModel;
import com.acme.kafka.OrderModel;
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

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.unprocessableEntity;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/rest")
@RequiredArgsConstructor
@Slf4j
public class Controller {
    private final Service service;

    @PostMapping(path = "/order", consumes = APPLICATION_JSON_VALUE)
    @SuppressWarnings("unused")
    ResponseEntity<Void> order(@RequestBody final OrderModel order) {
        service.order(order);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/confirm", consumes = APPLICATION_JSON_VALUE)
    @SuppressWarnings("unused")
    ResponseEntity<Void> confirm(@RequestBody final ConfirmationModel confirmation) {
        service.confirm(confirmation);
        return ResponseEntity.noContent().build();
    }

    @SuppressWarnings("unused")
    ResponseEntity<ProblemDetail> handleConstraintViolations(
            final List<String> violations
    ) {
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

    @ExceptionHandler(OrderConstraintViolationsException.class)
    @SuppressWarnings("unused")
    ResponseEntity<ProblemDetail> handleOrderConstraintViolations(
            final ConfirmationConstraintViolationsException ex
    ) {
        log.info("handleConstraintViolations: {}", ex.getMessage());

        final var violations = ex.getViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " +
                        violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName() + " " +
                        violation.getMessage())
                .toList();

        return handleConstraintViolations(violations);
    }

    @ExceptionHandler(ConfirmationConstraintViolationsException.class)
    @SuppressWarnings("unused")
    ResponseEntity<ProblemDetail> handleConfirmationConstraintViolations(
            final ConfirmationConstraintViolationsException ex
    ) {
        log.info("handleConstraintViolations: {}", ex.getMessage());

        final var violations = ex.getViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " +
                        violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName() + " " +
                        violation.getMessage())
                .toList();

        return handleConstraintViolations(violations);
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
