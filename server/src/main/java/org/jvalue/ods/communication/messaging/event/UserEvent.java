package org.jvalue.ods.communication.messaging.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jvalue.ods.auth.AuthUser;

import javax.validation.constraints.NotNull;

public class UserEvent extends MessagingEvent {
	@NotNull private UserEventType type;
	@NotNull private AuthUser data;

	@JsonCreator
	public UserEvent(@JsonProperty("type") UserEventType type, @JsonProperty("data") AuthUser user) {
		this.type = type;
		this.data = user;
	}

	public UserEventType getType() {
		return type;
	}

	public AuthUser getData() {
		return data;
	}

	public enum UserEventType {
		USER_CREATED,
		USER_UPDATED,
		USER_DELETED
	}
}
