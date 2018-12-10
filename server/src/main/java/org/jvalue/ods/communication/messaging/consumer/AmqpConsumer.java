package org.jvalue.ods.communication.messaging.consumer;

import org.jvalue.ods.communication.messaging.event.EventHandler;
import org.jvalue.ods.communication.messaging.event.MessagingEvent;

public interface AmqpConsumer<T extends MessagingEvent> {

	boolean connect();
	void close();

	void registerEventHandler(EventHandler<T> eventHandler);
}
