package com.danielptv.kafka;

import com.danielptv.rest.ConfirmationController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@KafkaListener(topics = "orders")
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {
    private final ConfirmationController confirmationController;
    @Value("${consumer.type}")
    private String consumerType;

    @KafkaHandler
    public void consume(final OrderModel order) {
        if (order.orderType().equalsIgnoreCase(consumerType)) {
            log.info("ORDER RECEIVED: orderId={}, orderType={}, message={}, producerEndpoint={}",
                    order.id(),
                    order.orderType(),
                    order.message(),
                    order.producerEndpoint());
            if (order.producerEndpoint() != null) {
                confirmationController.send(order);
            }
        }
    }
}
