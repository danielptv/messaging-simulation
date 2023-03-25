package com.danielptv.kafka;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderModel(
        @NotNull
        UUID id,
        @NotBlank
        String orderType,
        @NotBlank
        String message,
        String producerEndpoint
) {
}
