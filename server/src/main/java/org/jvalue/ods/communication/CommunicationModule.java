package org.jvalue.ods.communication;

import com.google.inject.AbstractModule;
import com.rabbitmq.client.ConnectionFactory;
import org.jvalue.ods.communication.messaging.MessagingConfig;
import org.jvalue.ods.communication.messaging.UserEventConsumer;

public class CommunicationModule extends AbstractModule {

	private final MessagingConfig messagingConfig;

	public CommunicationModule(MessagingConfig messagingConfig) {
		this.messagingConfig = messagingConfig;
	}

	@Override
	protected void configure() {
		UserEventConsumer userEventConsumer = new UserEventConsumer(new ConnectionFactory(), messagingConfig);
		if(!userEventConsumer.connect()) {
			throw new RuntimeException("Could not connect to RabbitMQ!");
		}
		bind(UserEventConsumer.class).toInstance(userEventConsumer);
	}
}
