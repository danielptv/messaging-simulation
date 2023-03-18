package com.danielptv.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {
    final OrderConfirmationProducer confirmationProducer;
    @Value("${consumer.type}")
    String consumerType;

    @KafkaListener(topics = "orders")
    public void consume(OrderDTO order) {
        if (order.producerId().equals(consumerType)) {
            log.info("MESSAGE RECEIVED: messageId={}, producerId={}, message={}, confirmation={}",
                    order.id(),
                    order.producerId(),
                    order.message(),
                    order.confirm());
            if (order.confirm()) {
                final var message = new ConfirmationModel(
                        UUID.randomUUID(),
                        order.id(),
                        order.producerId());
                confirmationProducer.send(message, "order-confirmations");
            }
        }
    }
}
