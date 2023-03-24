package com.danielptv.kafka;

import com.danielptv.rest.ConfirmationController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.JRE.JAVA_19;
import static org.junit.jupiter.api.condition.JRE.JAVA_20;

@Tag("integration")
@Tag("order-confirmations")
@DisplayName("Order confirmations")
@SpringBootTest
@AutoConfigureWireMock(port = 8092)
@EnabledForJreRange(min = JAVA_19, max = JAVA_20)
@SuppressWarnings("MagicNumber")
public class ConfirmationTest {
    private static final String PRODUCER_ENDPOINT = "http://localhost:8092";
    private final ConfirmationController controller = new ConfirmationController();
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    void setup() {
        stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())));
    }

    @Test
    @DisplayName("Send order confirmations to producers")
    void send() throws JsonProcessingException {
        // arrange
        final var order = OrderModel.builder()
                .id(UUID.randomUUID())
                .orderType("testOrderType")
                .producerEndpoint(PRODUCER_ENDPOINT)
                .build();

        // act
        final var response = controller.send(order);
        final var requests = findAll(postRequestedFor(urlEqualTo("/")));

        // assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(requests).isNotEmpty();
        final var confirmation = mapper.readValue(requests.get(0).getBodyAsString(), ConfirmationModel.class);
        assertThat(confirmation.orderId()).isEqualTo(order.id());
    }
}
