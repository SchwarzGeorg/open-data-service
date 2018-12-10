package org.jvalue.ods.communication.messaging.event;

public interface EventHandler<T extends MessagingEvent> {

	void handleEvent(T event);

	Class<T> getHandledEventClass();
}
