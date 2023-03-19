package com.danielptv.kafka;


import java.util.UUID;

public record OrderDTO(
        UUID id,
        String producerId,
        String message,
        boolean confirm
) {
}
