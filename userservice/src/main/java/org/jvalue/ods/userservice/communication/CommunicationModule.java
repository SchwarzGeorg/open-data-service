package org.jvalue.ods.userservice.communication;

import com.google.inject.AbstractModule;
import com.rabbitmq.client.ConnectionFactory;
import org.jvalue.ods.userservice.communication.messaging.MessagingConfig;
import org.jvalue.ods.userservice.communication.messaging.UserEventProducer;

public class CommunicationModule extends AbstractModule {

	private final MessagingConfig messagingConfig;

	public CommunicationModule(MessagingConfig messagingConfig) {
		this.messagingConfig = messagingConfig;
	}

	@Override
	protected void configure() {
		UserEventProducer userEventProducer = new UserEventProducer(new ConnectionFactory(), messagingConfig);
		bind(UserEventProducer.class).toInstance(userEventProducer);
	}
}
