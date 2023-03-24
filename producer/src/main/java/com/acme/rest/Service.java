package com.acme.rest;

import com.acme.kafka.ConfirmationModel;
import com.acme.kafka.OrderModel;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class Service {
    private final KafkaTemplate<String, OrderModel> template;
    private final Validator validator;
    private final Random random = new Random();

    @Value("${server.port}")
    private int serverPort;

    void order(@Valid final OrderModel order) {
        final OrderModel actualOrder = order.id() == null ? getRandomOrder() : order;
        log.info("order: {}", actualOrder);
        final var violations = validator.validate(actualOrder);
        if (!violations.isEmpty()) {
            log.debug("order: violations={}", violations);
            throw new OrderConstraintViolationsException(violations);
        }
        template.send("orders", actualOrder);
    }

    void confirm(@Valid final ConfirmationModel confirmation) {
        log.info("confirm: {}", confirmation);
        final var violations = validator.validate(confirmation);
        if (!violations.isEmpty()) {
            log.debug("confirm: violations={}", violations);
            throw new ConfirmationConstraintViolationsException(violations);
        }
    }


    private OrderModel getRandomOrder() {
        final var orderTypes = Arrays.asList("Software", "Hardware");
        final var messages = Arrays.asList(
                "I am a test order.",
                "Testing this simulation.",
                "Please put me in the queue",
                "Faster, faster!"
        );
        return OrderModel
                .builder()
                .id(UUID.randomUUID())
                .orderType(orderTypes.get(random.nextInt(orderTypes.size())))
                .message(messages.get(random.nextInt(messages.size())))
                .producerEndpoint(String.format("http://localhost:%s/rest/confirm", serverPort))
                .build();
    }
}
