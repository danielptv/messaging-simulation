package com.danielptv.rest;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ConfirmationModel(
        UUID id,
        UUID orderId,
        String orderType
) {
}
