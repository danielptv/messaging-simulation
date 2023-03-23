package com.danielptv.kafka;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ConfirmationModel(
        UUID id,
        UUID orderId,
        String orderType
) {
}
