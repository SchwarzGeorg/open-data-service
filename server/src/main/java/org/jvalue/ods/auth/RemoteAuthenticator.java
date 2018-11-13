package org.jvalue.ods.auth;

import com.google.common.base.Optional;
import org.jvalue.commons.auth.Authenticator;
import org.jvalue.commons.auth.User;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

public class RemoteAuthenticator implements Authenticator {

	// TODO: get this from somewhere else (e.g. service discovery)
	public static final String USER_SERVICE_PATH = "http://localhost:8080/ods/api/v1";

	public static final String USER_SERVICE_AUTH_HEADER = "Authorization";

	@Inject
	public RemoteAuthenticator() {
	}

	@Override
	public Optional<User> authenticate(String authHeader) {
		// get user from UserService
		Optional<User> user = getUserFromUserService(authHeader);

		// TODO: cache result

		// TODO: react on changes via RabbitMQ
		return user;
	}

	private Optional<User> getUserFromUserService(String authHeader) {
		Client client = ClientBuilder.newClient();
		// TODO: make retries if no connection

		return Optional.of(
			client
				.target(USER_SERVICE_PATH + "/users")
				.request(MediaType.APPLICATION_JSON)
				.header(USER_SERVICE_AUTH_HEADER, authHeader)
				.get(User.class)
		);
	}
}
