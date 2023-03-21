package com.danielptv.kafka;


import java.util.UUID;

public record OrderModel(
        UUID id,
        String orderType,
        String message,
        String producerEndpoint
) {
}
