package org.jvalue.ods.auth;

import com.google.common.base.Optional;
import org.jvalue.commons.auth.Authenticator;
import org.jvalue.commons.auth.User;

import javax.inject.Inject;

public class RemoteAuthenticator implements Authenticator {


	private final RemoteAuthenticationClient remoteAuthenticationClient;

	@Inject
	public RemoteAuthenticator(
		RemoteAuthenticationClient remoteAuthenticationClient
	) {
		this.remoteAuthenticationClient = remoteAuthenticationClient;
	}

	@Override
	public Optional<User> authenticate(String authHeader) {
		// get user from UserService
		Optional<User> user = remoteAuthenticationClient.authenticate(authHeader);

		// TODO: cache result

		// TODO: react on changes via RabbitMQ
		return user;
	}
}
