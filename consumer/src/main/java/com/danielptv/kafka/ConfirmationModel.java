package com.danielptv.kafka;

import java.util.UUID;

public record ConfirmationModel(
        UUID id,
        UUID messageId,
        String producerId
) {
}
