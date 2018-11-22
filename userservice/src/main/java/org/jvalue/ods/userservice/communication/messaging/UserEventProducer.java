package org.jvalue.ods.userservice.communication.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.ConnectionFactory;
import org.jvalue.commons.utils.Log;
import org.jvalue.ods.userservice.utils.JsonMapper;

import java.nio.charset.StandardCharsets;

public class UserEventProducer extends AbstractProducer {

	private static final String ROUTING_KEY = "user-events";
	private static final String EXCHANGE_NAME = "user-exchange";
	private static final String EXCHANGE_TYPE = "fanout";


	public UserEventProducer(ConnectionFactory factory, MessagingConfig messagingConfig) {
		super(factory, messagingConfig, EXCHANGE_NAME, EXCHANGE_TYPE, ROUTING_KEY);
	}

	public boolean publishUserEvent(UserEvent userEvent) {
		String message = null;
		try {
			message = JsonMapper.writeValueAsString(userEvent);
			return super.produce(message.getBytes(StandardCharsets.UTF_8));
		} catch (JsonProcessingException e) {
			Log.error("Could not convert UserEvent to JSON object!");
			return false;
		}
	}
}
