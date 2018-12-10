package org.jvalue.ods.userservice.communication.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.ConnectionFactory;
import org.jvalue.commons.utils.Log;
import org.jvalue.ods.userservice.utils.JsonMapper;

import java.nio.charset.StandardCharsets;

public class UserEventProducer {
	private static final String EXCHANGE_NAME = "user-event-exchange";
	private static final String EXCHANGE_TYPE = "topic";

	private final MessagingConfig messagingConfig;

	private final AmqProducer amqProducer;

	public UserEventProducer(ConnectionFactory factory, MessagingConfig messagingConfig) {
		this.amqProducer = new RabbitmqProducer(factory, messagingConfig, EXCHANGE_NAME, EXCHANGE_TYPE);
		this.messagingConfig = messagingConfig;

		if(!amqProducer.connect()) {
			throw new RuntimeException("Could not connect to RabbitMQ!");
		}
	}

	public boolean publishUserEvent(UserEvent userEvent) {
		String message = null;
		try {
			message = JsonMapper.writeValueAsString(userEvent);
			return amqProducer.produce(userEvent.getType().getRoutingKey(), message.getBytes(StandardCharsets.UTF_8));
		} catch (JsonProcessingException e) {
			Log.error("Could not convert UserEvent to JSON object!");
			return false;
		}
	}


}
