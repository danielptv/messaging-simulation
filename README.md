# Messaging Simulation

This project is a simulation for messaging consisting of 3 main parts:
* **Producers** produce orders.
* **Consumers** consume the orders and send confirmation messages back to the producers. Different consumers process different types of orders.
* **Kafka** as message-oriented middleware (MOM) for sending out orders to consumers. The confirmations are being sent by RESTful calls.

## Usage


## Implementation and Structure
The project consists of the two microservices 'consumer' and 'producer' as well as Kafka as message-oriented middleware. The microservices are both implemented as Spring Boot applications. Below you can find explanations of some of their main aspects in terms of structure and implementation.

**Consumer** ─ Receives messages from producers and sends back confirmations to them.
````text
consumer/src
 └── main
      ├── java/com.danielptv
      │     ├── dev/CustomErrorHandler.java
      │     ├── kafka/OrderConsumer.java
      │     ├── rest/ConfirmationController.java
      │     └── Application.java
      └── resources/application.yml
````
* **OrderConsumer.java:** Implements a Kafka message listener (@KafkaListener) to receive messages from the queue. If the order type matches the consumer type the message will be processed and a confirmation will be sent by calling the ConfirmationController.
* **ConfirmationController.java:** Sends confirmation messages upon call. The confirmations are sent as POST request to the API endpoint of the producer corresponding to the passed order. The correct endpoint must be provided by the order-message.
* **CustomErrorHandler.java:** Provides custom error handling for exceptions thrown within the Kafka listener.
* **application.yml:** Configuration for spring-kafka and other important parameters. Parameterization enables this server to be a generic consumer with the consumer type being passed through an environment variable.
