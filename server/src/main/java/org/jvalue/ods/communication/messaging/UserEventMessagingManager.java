package org.jvalue.ods.communication.messaging;

import com.rabbitmq.client.*;
import org.jvalue.ods.communication.messaging.consumer.AmqpConsumer;
import org.jvalue.ods.communication.messaging.consumer.RabbitmqConsumer;
import org.jvalue.ods.communication.messaging.event.UserEvent;
import org.jvalue.ods.communication.messaging.event.UserEventHandler;

public class UserEventMessagingManager {

	private static final String EXCHANGE_NAME = "user-event-exchange";
	private static final String EXCHANGE_TYPE = "topic";
	private static final String ROUTING_KEY = "user.#"; // user.anything

	private final AmqpConsumer<UserEvent> amqpConsumer;

	public UserEventMessagingManager(ConnectionFactory factory, MessagingConfig messagingConfig) {
		this.amqpConsumer = new RabbitmqConsumer<UserEvent>(factory, messagingConfig, EXCHANGE_NAME, EXCHANGE_TYPE, ROUTING_KEY);
		if(!amqpConsumer.connect()) {
			throw new RuntimeException("Could not connect to RabbitMQ!");
		}
	}

	public void registerEventHandler(UserEventHandler eventHandler) {
		amqpConsumer.registerEventHandler(eventHandler);
	}

}
