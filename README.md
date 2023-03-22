# Messaging Simulation

This project is a simulation for messaging consisting of 3 main parts:
* **Producers** produce orders.
* **Consumers** consume the orders and send confirmation messages back to the producers. Different consumers process different types of orders.
* **Kafka** as message-oriented middleware (MOM) for sending out orders to consumers. The confirmations are being sent by RESTful calls.
