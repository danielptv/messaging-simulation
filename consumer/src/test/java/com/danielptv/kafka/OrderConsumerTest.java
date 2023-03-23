package com.danielptv.kafka;

import com.danielptv.rest.ConfirmationController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.condition.JRE.JAVA_19;
import static org.junit.jupiter.api.condition.JRE.JAVA_20;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@Tag("kafka")
@Tag("kafka-consumer")
@DisplayName("Kafka consumer for orders")
@SpringBootTest
@EmbeddedKafka(topics = "orders", partitions = 1)
@TestPropertySource(
        locations = "classpath:application.yml",
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "CONSUMER_TYPE=hardware"
        })
@EnabledForJreRange(min = JAVA_19, max = JAVA_20)
@AutoConfigureWireMock(port = 8092)
public class OrderConsumerTest {
    @Autowired
    private KafkaTemplate<String, OrderModel> sender;
    @Autowired
    private OrderConsumer consumer;
    @Value("${CONSUMER_TYPE}")
    private String validConsumer;

    @Test
    @DisplayName("Message consumption with matching order type")
    public void consumeMessage() throws InterruptedException {
        // arrange
        final var order = OrderModel
                .builder()
                .id(UUID.randomUUID())
                .orderType(validConsumer)
                .message("This is a test.")
                .producerEndpoint("http://localhost:8092")
                .build();

        // act
        sender.send("orders", order);
        Thread.sleep(1000);

        // assert
        assertThat(consumer.payload).isNotNull();
        assertEquals(order, consumer.payload);
    }

    @Test
    @DisplayName("Message consumption with wrong producer id")
    public void dontConsumeMessage() throws InterruptedException {
        // arrange
        final var order = OrderModel
                .builder()
                .id(UUID.randomUUID())
                .orderType("fff-fff")
                .message("This is a test.")
                .producerEndpoint("http://localhost:8092")
                .build();

        // act
        sender.send("orders", order);
        Thread.sleep(1000);

        // assert
        assertThat(consumer.payload).isNull();
    }

    @Test
    @DisplayName("Message consumption and confirmation sending")
    public void consumeAndConfirm() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        // arrange
        stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(201)));
        final var order = OrderModel
                .builder()
                .id(UUID.randomUUID())
                .orderType(validConsumer)
                .message("This is a test.")
                .producerEndpoint("http://localhost:8092")
                .build();

        // act
        sender.send("orders", order);
        Thread.sleep(1000);

        final var controller = consumer.confirmationController;
        var responsePrivate = ConfirmationController.class.getDeclaredField("response");
        responsePrivate.setAccessible(true);
        final var response = (ResponseEntity<?>) responsePrivate.get(controller);
        var confirmationPrivate = ConfirmationController.class.getDeclaredField("confirmation");
        confirmationPrivate.setAccessible(true);
        final var confirmation = (ConfirmationModel) confirmationPrivate.get(controller);

        // assert
        assertThat(consumer.payload).isNotNull();
        assertEquals(order, consumer.payload);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(confirmation.orderId()).isEqualTo(order.id());
        assertThat(confirmation.orderType()).isEqualTo(validConsumer);
    }
}
