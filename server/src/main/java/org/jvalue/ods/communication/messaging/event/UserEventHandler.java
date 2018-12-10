package org.jvalue.ods.communication.messaging.event;

public abstract class UserEventHandler implements EventHandler<UserEvent> {

	@Override
	public abstract void handleEvent(UserEvent event);

	@Override
	public Class<UserEvent> getHandledEventClass() {
		return UserEvent.class;
	}
}
