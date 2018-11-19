package org.jvalue.ods.auth;

import com.google.common.base.Optional;
import org.jvalue.commons.auth.User;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class UserServiceAuthenticationClient implements RemoteAuthenticationClient {

	// TODO: get this from service discovery
	public final String userServicePath;

	@Inject
	public UserServiceAuthenticationClient(
		AuthConfig authConfig
	) {
		userServicePath = authConfig.getUserServiceUrl();
	}

	@Override
	public Optional<User> authenticate(String authHeader) {

		Client client = ClientBuilder.newClient();
		// TODO: make retries if no connection

		return Optional.of(
			client
				.target(userServicePath + "/users/me")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, authHeader)
				.get(User.class)
		);
	}
}
