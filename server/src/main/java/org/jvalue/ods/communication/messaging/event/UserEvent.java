package org.jvalue.ods.communication.messaging.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class UserEvent extends MessagingEvent {
	@NotNull private UserEventType type;
	@NotNull private String userId;

	@JsonCreator
	public UserEvent(@JsonProperty("type") UserEventType type, @JsonProperty("userId") String userId) {
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
