package com.danielptv.rest;

import com.danielptv.kafka.ConfirmationModel;
import com.danielptv.kafka.OrderModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@Slf4j
public class ConfirmationController {
    ConfirmationModel confirmation;
    ResponseEntity<?> response;
    public void send(OrderModel order) {
        confirmation = null;
        response = null;
        final var restTemplate = new RestTemplate();
        confirmation = ConfirmationModel
                .builder()
                .id(UUID.randomUUID())
                .orderId(order.id())
                .orderType(order.orderType())
                .build();

        response = restTemplate.postForEntity(order.producerEndpoint(), confirmation, ConfirmationModel.class);
        log.info("SENDING CONFIRMATION: message={}, statusCode={}", confirmation, response.getStatusCode());
    }
}
