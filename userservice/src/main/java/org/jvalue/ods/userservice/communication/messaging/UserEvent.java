package org.jvalue.ods.userservice.communication.messaging;

public class UserEvent {
	private UserEventType type;
	private String userId;

	public UserEvent(UserEventType type, String userId) {
		this.type = type;
		this.userId = userId;
	}

	public UserEventType getType() {
		return type;
	}

	public String getUserId() {
		return userId;
	}

	public enum UserEventType {
		USER_CREATED,
		USER_UPDATED,
		USER_DELETED
	}
}
