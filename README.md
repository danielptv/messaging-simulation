# Messaging Simulation

## About

This project is a simulation for messaging consisting of 3 main parts:

* **Apache Kafka** as message-oriented middleware (MOM) and distributed messaging system.
* **Producers** produce messages (orders) and send them to the queue.
* **Consumers** consume the orders from the queue and send confirmation messages back to the producers. Each consumer
  can process exactly one type of order. The confirmations are being sent
  by RESTful calls.

## Usage

### Prerequisites

To run this simulation you will need the following software:

* Installation of [Docker](https://docs.docker.com/get-docker/)
* JDK min. version 19,
  i.e. [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=19)
* Recent version of [Gradle](https://services.gradle.org/distributions/) to create Docker images and use Gradle CLI
* [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) if you don't want to manually download the
  project

### How To Run (build Docker images)

1. Clone the repo: `git clone https://github.com/danielptv/messaging-simulation.git`
2. Create Docker images of **consumer** and **producer** by running `gradle bootBuildImage` in their respective root
   directories.
3. Start the servers by calling `docker compose up` in the projects' root directory using the provided <code>
   *docker-compose.yaml*</code>.
4. Done!

**Note:** Producer and Kafka ports as well as consumer types can be adjusted in the <code>*.env*</code> file.

To ensure code quality, you can use `gradle spotbugsmain checkstylemain` to lint the code.

### How To Run (use Gradle developer mode)

1. Clone the repo: `git clone https://github.com/danielptv/messaging-simulation.git`
2. Start Kafka by calling `docker compose up zookeeper kafka` in the projects' root directory using the provided <code>
   *docker-compose.yaml*</code>.
3. Start consumers and producers by running `gradle bootRun` repeatedly in their respective root
   directories. **Important:** Make sure to specify ports by passing them as options to avoid conflicts. For consumers,
   also define their type as it will default to *"software"* otherwise.

Below examples show how to start a producer and a consumer server with `gradle bootRun`:

````shell
cd /messaging-simulation/producer
gradle bootRun -Dport=8082
````

````shell
cd /messaging-simulation/consumer
gradle bootRun -Dport=8086 -Dtype=hardware
````

### How To Simulate

#### Basic Scenario (all servers running)

Once you have the project up and running, simulating the messaging is quite easy. You can use the built-in IntelliJ HTTP
Client or any other tool like Postman to place orders (aka send messages).

For the IntelliJ HTTP Client you can find a predefined set of messages in the <code>*POST-orders.http*</code> file that
you can run out of the box. **Important:** Adjust the ports in <code>*http-client.env.json*</code> to the ones you have
set when starting up the servers.

Now you are ready to start sending out messages. Every time a message is sent out or processed the respective server
will log it. Watch the logs carefully!

#### Advanced Scenarios

To test out more advanced scenarios like the consumer being unavailable and Kafka therefore queuing up messages you can
shut down and start the servers selectively.

If your servers are Docker images (the first running option described
above) use `docker compose stop <container_name>` to stop and `docker compose up <container_name>` to start a server.
Otherwise, stop your servers with the CTRL+C shortcut.

Once again, you can now send messages and see what will happen. Happy simulating!

## Implementation and Structure

The project consists of the two microservices 'consumer' and 'producer' as well as Apache Kafka as message-oriented
middleware.
The microservices are both implemented as Spring Boot applications. Below you can find explanations of some of their
main aspects in terms of structure and implementation.

**Apache Kafka** ─ Distributed messaging system.

Kafka is configured in the <code>
*docker-compose.yaml*</code> using the confluentinc/cp-kafka image (Kafka Community Edition).

**Producer** ─ Sends messages to the queue and receives confirmations from consumers.

````text
producer/src
 └── main
      ├── java/com.acme
      │     ├── rest/Controller.java
      │     ├── rest/Service.java
      │     └── Application.java
      └── resources/application.yml
````

* **Controller.java:** Implements a simple REST endpoint for receiving confirmations and triggering orders.
* **Service.java:** Sends order messages using Kafka. The methods are called by the controller.
* **application.yml:** Configuration for spring-kafka and other important parameters. Parameterization enables this
  server to be a generic consumer with the consumer type being passed through an environment variable.

**Consumer** ─ Receives messages from the queue and sends back confirmations to the respective producers.

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

* **OrderConsumer.java:** Implements a Kafka message listener (@KafkaListener) to receive messages from the queue. If
  the order type matches the consumer type the message will be processed and a confirmation will be sent by calling the
  ConfirmationController.
* **ConfirmationController.java:** Sends confirmation messages upon call. The confirmations are sent as POST request to
  the API endpoint of the producer corresponding to the passed order. The correct endpoint must be provided by the
  order-message.
* **CustomErrorHandler.java:** Provides custom error handling for exceptions thrown within the Kafka listener.
* **application.yml:** Configuration for spring-kafka and other important parameters. Parameterization enables this
  server to be a generic consumer with the consumer type being passed through an environment variable.
