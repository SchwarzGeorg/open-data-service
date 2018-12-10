package org.jvalue.ods.userservice.communication.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jvalue.ods.userservice.user.User;

import javax.validation.constraints.NotNull;

public class UserEvent {
	@NotNull private UserEventType type;
	@NotNull private User data;

	@JsonCreator
	public UserEvent(@JsonProperty("type") UserEventType type, @JsonProperty("data") User data) {
		this.type = type;
		this.data = data;
	}

	public UserEventType getType() {
		return type;
	}

	public User getData() {
		return data;
	}

	public enum UserEventType {
		USER_CREATED("user.created"),
		USER_UPDATED("user.updated"),
		USER_DELETED("user.deleted");

		String routingKey;
		UserEventType(String routingKey) {
			this.routingKey = routingKey;
		}

		String getRoutingKey() {
			return this.routingKey;
		}
	}
}
