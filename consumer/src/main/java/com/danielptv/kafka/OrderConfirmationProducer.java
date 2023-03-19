package com.danielptv.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmationProducer {
    final KafkaTemplate<String, ConfirmationModel> template;
    CompletableFuture<SendResult<String, ConfirmationModel>> payload;

    public void send(ConfirmationModel message, String topic) {
        payload = null;
        log.info("SENDING CONFIRMATION: topic={}, message={}", topic, message);
        payload = template.send(topic, message);
    }
}
