package com.danielptv.kafka;

final class OrderConstraintViolationsException extends RuntimeException {
    OrderConstraintViolationsException() {
        super("Constraints are violated");
    }
}
