package com.danielptv.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmationProducer {
    final KafkaTemplate<String, ConfirmationModel> template;

    public void send(ConfirmationModel message, String topic) {
        template.send(topic, message);
    }
}
