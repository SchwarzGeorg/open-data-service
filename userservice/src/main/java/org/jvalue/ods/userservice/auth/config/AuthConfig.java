package org.jvalue.ods.userservice.auth.config;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jvalue.ods.userservice.models.BasicAuthUserDescription;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AuthConfig {

	// basic auth
	@NotNull @Valid private final List<BasicAuthUserDescription> users;

	// oauth
	@NotNull private final String googleOAuthWebClientId;
	@NotNull private final List<String> googleOAuthClientIds;

	@JsonCreator
	public AuthConfig(
			@JsonProperty("users") List<BasicAuthUserDescription> users,
			@JsonProperty("googleOAuthWebClientId") String googleOAuthWebClientId,
			@JsonProperty("googleOAuthClientIds") List<String> googleOAuthClientIds) {

		this.users = users;
		this.googleOAuthWebClientId = googleOAuthWebClientId;
		this.googleOAuthClientIds = googleOAuthClientIds;
	}


	public List<BasicAuthUserDescription> getUsers() {
		return users;
	}


	public String getGoogleOAuthWebClientId() {
		return googleOAuthWebClientId;
	}


	public List<String> getGoogleOAuthClientIds() {
		return googleOAuthClientIds;
	}

}
