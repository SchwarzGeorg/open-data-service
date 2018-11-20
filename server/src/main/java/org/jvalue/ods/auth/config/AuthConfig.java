package org.jvalue.ods.auth.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class AuthConfig {

	@NotNull private final String userServiceUrl;

	@JsonCreator
	public AuthConfig(
		@JsonProperty("userServiceUrl") String userServiceUrl) {

		this.userServiceUrl = userServiceUrl;
	}


	public String getUserServiceUrl() {
		return userServiceUrl;
	}
}
