package org.jvalue.ods.auth.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class AuthConfig {

	@NotNull private final String userServiceUrl;
	@NotNull private final String userServiceHealthcheckUrl;

	@JsonCreator
	public AuthConfig(
		@JsonProperty("userServiceUrl") String userServiceUrl,
		@JsonProperty("userServiceHealthcheckUrl") String userServiceHealthcheckUrl) {

		this.userServiceUrl = userServiceUrl;
		this.userServiceHealthcheckUrl = userServiceHealthcheckUrl;
	}


	public String getUserServiceUrl() {
		return userServiceUrl;
	}

	public String getUserServiceHealthcheckUrl() {
		return userServiceHealthcheckUrl;
	}
}
