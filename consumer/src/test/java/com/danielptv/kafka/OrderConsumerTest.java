package com.danielptv.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.condition.JRE.JAVA_19;
import static org.junit.jupiter.api.condition.JRE.JAVA_20;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@Tag("kafka")
@Tag("kafka-consumer")
@DisplayName("Kafka consumer for orders")
@SpringBootTest
@EmbeddedKafka(topics = {"orders", "order-confirmations"}, partitions = 1)
@TestPropertySource(
        locations = "classpath:application.yml",
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "CONSUMER_TYPE=hardware"
        })
@EnabledForJreRange(min = JAVA_19, max = JAVA_20)
public class OrderConsumerTest {
    @Autowired
    private KafkaTemplate<String, OrderDTO> sender;
    @Autowired
    private OrderConsumer consumer;
    @Autowired
    private OrderConfirmationProducer confirmationProducer;
    @Value("${CONSUMER_TYPE}")
    private String validConsumer;

    @Test
    @DisplayName("Message consumption with matching producer id")
    public void consumeMessage() throws InterruptedException {
        // arrange
        OrderDTO order = new OrderDTO(UUID.randomUUID(), validConsumer, "test-message", true);
        // act
        sender.send("orders", order);
        Thread.sleep(5000);
        // assert
        assertThat(consumer.payload).isNotNull();
        assertEquals(order, consumer.payload);
    }

    @Test
    @DisplayName("Message consumption with wrong producer id")
    public void dontConsumeMessage() throws InterruptedException {
        // arrange
        OrderDTO order = new OrderDTO(UUID.randomUUID(), "fff-fff", "test-message", true);
        // act
        sender.send("orders", order);
        Thread.sleep(5000);
        // assert
        assertThat(consumer.payload).isNull();
    }

    @Test
    @DisplayName("Sending confirmation after successfully processing order")
    public void sendConfirmation() throws InterruptedException, ExecutionException {
        // arrange
        OrderDTO order = new OrderDTO(UUID.randomUUID(), validConsumer, "test-message", true);
        // act
        sender.send("orders", order);
        Thread.sleep(5000);
        // assert
        assertThat(consumer.payload).isNotNull();
        assertEquals(order, consumer.payload);

        assertThat(confirmationProducer.payload).isNotNull();
        assertThat(confirmationProducer.payload.get().getProducerRecord().topic())
                .isNotNull()
                .isInstanceOf(String.class)
                .isEqualTo("order-confirmations");
        assertThat(confirmationProducer.payload.get().getProducerRecord().value())
                .isNotNull()
                .isInstanceOf(ConfirmationModel.class);
        assertThat(confirmationProducer.payload.get().getProducerRecord().value().messageId())
                .isEqualTo(order.id());
    }
}
