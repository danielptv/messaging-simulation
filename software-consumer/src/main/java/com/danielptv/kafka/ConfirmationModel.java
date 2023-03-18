package com.danielptv.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public record ConfirmationModel(
        UUID id,
        UUID messageId,
        String producerId
) {
    String toJSON() {
        final var mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException();
        }
    }
}
