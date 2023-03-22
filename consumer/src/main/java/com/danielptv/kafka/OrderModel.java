package com.danielptv.kafka;


import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderModel(
        UUID id,
        String orderType,
        String message,
        String producerEndpoint
) {
}
