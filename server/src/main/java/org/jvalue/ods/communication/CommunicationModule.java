package org.jvalue.ods.communication;

import com.google.inject.AbstractModule;
import org.jvalue.ods.communication.messaging.MessagingConfig;

public class CommunicationModule extends AbstractModule {

	private final MessagingConfig messagingConfig;

	public CommunicationModule(MessagingConfig messagingConfig) {
		this.messagingConfig = messagingConfig;
	}

	@Override
	protected void configure() {
		// TODO
	}
}
