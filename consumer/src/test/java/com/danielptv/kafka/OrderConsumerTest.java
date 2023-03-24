package com.danielptv.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
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
        properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}", "CONSUMER_TYPE=hardware"})
@EnabledForJreRange(min = JAVA_19, max = JAVA_20)
@AutoConfigureWireMock(port = 8092)
@SuppressWarnings("MagicNumber")
public class OrderConsumerTest {
    @Autowired
    private KafkaTemplate<String, OrderModel> sender;
    @Value("${CONSUMER_TYPE}")
    private String validConsumer;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    void setup() {
        stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())));
    }

    @Test
    @DisplayName("Message consumption with correct order type and confirmation sending")
    public void consumeAndConfirm() throws InterruptedException, JsonProcessingException {
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
        final var requests = findAll(postRequestedFor(urlEqualTo("/")));
        final var confirmation = mapper.readValue(requests.get(0).getBodyAsString(), ConfirmationModel.class);

        // assert
        assertThat(confirmation.orderId()).isEqualTo(order.id());
        assertThat(confirmation.orderType()).isEqualTo(validConsumer);
    }

    @Test
    @DisplayName("Message consumption with wrong order type")
    public void dontConsume() throws InterruptedException {
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
        final var requests = findAll(postRequestedFor(urlEqualTo("/")));

        // assert
        assertThat(requests).isEmpty();
    }
}
