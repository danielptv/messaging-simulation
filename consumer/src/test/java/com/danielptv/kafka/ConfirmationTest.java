package com.danielptv.kafka;

import com.danielptv.rest.ConfirmationController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
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
public class ConfirmationTest {
    private static final String PRODUCER_ENDPOINT = "http://localhost:8092";
    ConfirmationController controller;

    @BeforeAll
    void setup() {
        controller = new ConfirmationController();
        stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(201)));
    }

    @Test
    @DisplayName("Send order confirmations to producers")
    void send() throws NoSuchFieldException, IllegalAccessException {
        // arrange
        var order = OrderModel.builder()
                .id(UUID.randomUUID())
                .orderType("testOrderType")
                .producerEndpoint(PRODUCER_ENDPOINT)
                .build();

        // act
        controller.send(order);
        var responsePrivate = ConfirmationController.class.getDeclaredField("response");
        responsePrivate.setAccessible(true);
        final var response = (ResponseEntity<?>) responsePrivate.get(controller);
        var confirmationPrivate = ConfirmationController.class.getDeclaredField("confirmation");
        confirmationPrivate.setAccessible(true);
        final var confirmation = (ConfirmationModel) confirmationPrivate.get(controller);

        // assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(confirmation.orderId()).isEqualTo(order.id());
    }
}
