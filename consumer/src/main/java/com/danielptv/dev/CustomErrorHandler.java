package com.danielptv.dev;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

@Component
@Slf4j
public class CustomErrorHandler implements CommonErrorHandler {
    @Override
    public boolean handleOne(
            Exception thrownException,
            ConsumerRecord<?, ?> record,
            Consumer<?, ?> consumer,
            MessageListenerContainer container
    ) {
        switch (thrownException.getCause()) {
            case final ResourceAccessException ex -> handleResourceAccessException(ex);
            case final DeserializationException ex -> handleDeserializationException(ex);
            default -> handleOtherExceptions(thrownException.getCause());
        }
        return true;
    }

    @ExceptionHandler
    public void handleResourceAccessException(ResourceAccessException ex) {
        log.error("ResourceAccessException: message={}", ex.getLocalizedMessage());
    }

    @ExceptionHandler
    public void handleDeserializationException(DeserializationException ex) {
        log.error("DeserializationException: message={}", ex.getLocalizedMessage());
    }

    @ExceptionHandler
    public void handleOtherExceptions(Throwable ex) {
        log.error("Other exception: message={}", ex.getLocalizedMessage());
    }
}
