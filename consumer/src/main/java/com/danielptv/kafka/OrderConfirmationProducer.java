package com.danielptv.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmationProducer {
    final KafkaTemplate<String, String> template;

    public void send(ConfirmationModel message, String topic) {
        final var jsonMessage = message.toJSON();
        log.info("SENDING CONFIRMATION: topic={}, message={}", topic, jsonMessage);
        template.send(topic, jsonMessage);
    }
}
