package com.danielptv.kafka;

import com.danielptv.rest.ConfirmationController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {
    final ConfirmationController confirmationController;
    @Value("${consumer.type}")
    String consumerType;
    OrderModel payload;

    @KafkaListener(topics = "orders")
    public void consume(OrderModel order) {
        payload = null;
        if (order.orderType().equals(consumerType)) {
            log.info("MESSAGE RECEIVED: orderId={}, orderType={}, message={}, producerEndpoint={}",
                    order.id(),
                    order.orderType(),
                    order.message(),
                    order.producerEndpoint());
            payload = order;
            if (order.producerEndpoint() != null) {
                confirmationController.send(order);
            }
        }
    }
}
