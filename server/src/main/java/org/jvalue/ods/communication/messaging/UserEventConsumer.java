package org.jvalue.ods.communication.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

public class UserEventConsumer extends AbstractConsumer {

	private static final String EXCHANGE_NAME = "user-exchange";
	private static final String EXCHANGE_TYPE = "fanout";
	private final MessagingConfig messagingConfig;


	public UserEventConsumer(ConnectionFactory factory, MessagingConfig messagingConfig) {
		super(factory, messagingConfig);
		this.messagingConfig = messagingConfig;
	}

	@Override
	protected void doSetupBroker(Channel channel) throws IOException {
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, EXCHANGE_NAME, "");
	}

	@Override
	public String toString() {
		return null;
	}
}
