package com.acme.rest;

import com.acme.kafka.ConfirmationModel;
import com.acme.kafka.OrderModel;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import static com.acme.dev.Banner.LOCALHOST;


@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class Service {
    private final KafkaTemplate<String, OrderModel> template;
    private final Validator validator;
    @Value("${server.port}")
    private int serverPort;

    void order(@Valid final OrderModel order) {
        final var violations = validator.validate(order);
        if (!violations.isEmpty()) {
            log.debug("order: violations={}", violations);
            throw new OrderConstraintViolationsException(violations);
        }
        final var actualOrder = OrderModel.builder()
                .id(order.id())
                .orderType(order.orderType())
                .message(order.message())
                .producerEndpoint(order.producerEndpoint() == null
                        ? String.format("http://%s:%s/rest/confirm", LOCALHOST.getHostAddress(), serverPort)
                        : order.producerEndpoint())
                .build();
        log.info("SENDING ORDER: orderId={}, orderType={}, message={}, producerEndpoint={}",
                actualOrder.id(),
                actualOrder.orderType(),
                actualOrder.message(),
                actualOrder.producerEndpoint());
        template.send("orders", actualOrder);
    }

    void confirm(@Valid final ConfirmationModel confirmation) {
        final var violations = validator.validate(confirmation);
        if (!violations.isEmpty()) {
            log.debug("confirm: violations={}", violations);
            throw new ConfirmationConstraintViolationsException(violations);
        }
        log.info("CONFIRMATION RECEIVED: confirmationId={}, orderId={}, orderType={}",
                confirmation.id(),
                confirmation.orderId(),
                confirmation.orderType());
    }
}
