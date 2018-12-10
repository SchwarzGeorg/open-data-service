package org.jvalue.ods.communication;

import com.google.inject.AbstractModule;
import com.rabbitmq.client.ConnectionFactory;
import org.jvalue.ods.communication.messaging.MessagingConfig;
import org.jvalue.ods.communication.messaging.UserEventMessagingManager;

public class CommunicationModule extends AbstractModule {

	private final MessagingConfig messagingConfig;

	public CommunicationModule(MessagingConfig messagingConfig) {
		this.messagingConfig = messagingConfig;
	}

	@Override
	protected void configure() {
		UserEventMessagingManager userEventMessagingManager = new UserEventMessagingManager(new ConnectionFactory(), messagingConfig);
		bind(UserEventMessagingManager.class).toInstance(userEventMessagingManager);
	}
}
