package org.jvalue.ods.userservice.communication.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.jvalue.commons.utils.Log;
import org.jvalue.ods.userservice.utils.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UserEventProducer extends AbstractProducer{

	private static final String EXCHANGE_NAME = "user-event-exchange";
	private static final String EXCHANGE_TYPE = "topic";

	private final MessagingConfig messagingConfig;


	public UserEventProducer(ConnectionFactory factory, MessagingConfig messagingConfig) {
		super(factory, messagingConfig);
		this.messagingConfig = messagingConfig;
	}

	public boolean publishUserEvent(UserEvent userEvent) {
		String message = null;
		try {
			message = JsonMapper.writeValueAsString(userEvent);
			return super.produce(userEvent.getType().getRoutingKey(), message.getBytes(StandardCharsets.UTF_8));
		} catch (JsonProcessingException e) {
			Log.error("Could not convert UserEvent to JSON object!");
			return false;
		}
	}

	@Override
	protected void doSetupBroker(Channel channel) throws IOException {
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
	}

	@Override
	protected void doProduce(Channel channel, String routingKey, byte[] message) throws IOException {
		channel.basicPublish(EXCHANGE_NAME, routingKey, null, message);
	}

	@Override
	public String toString() {
		return "{brokerHost: '" + messagingConfig.getBrokerHost() +
			"', brokerVHost: '" + messagingConfig.getBrokerVHost() +
			"', brokerPort: '" + messagingConfig.getBrokerPort() +
			"', exchange_name: '" + EXCHANGE_NAME +
			"', exchange_type: '" + EXCHANGE_TYPE + "'}";
	}
}
