package org.jvalue.ods.auth.authenticator;

import com.google.common.base.Optional;
import org.jvalue.ods.auth.User;

import javax.inject.Inject;

public class RemoteAuthenticator implements Authenticator {


	private final RemoteAuthenticationClient remoteAuthenticationClient;
	private final AuthCache cache;

	@Inject
	public RemoteAuthenticator(
		RemoteAuthenticationClient remoteAuthenticationClient,
		AuthCache authCache
	) {
		this.remoteAuthenticationClient = remoteAuthenticationClient;
		this.cache = authCache;
	}

	@Override
	public Optional<User> authenticate(String authHeader) {
		User user = cache.getUser(authHeader);
		if(user != null) {
			// put hit
			return Optional.of(user);
		}

		// get user from UserService
		Optional<User> userOptional = remoteAuthenticationClient.authenticate(authHeader);

		if(userOptional.isPresent()) {
			// put user
			cache.put(authHeader, userOptional.get());
		}

		// TODO: react on changes via RabbitMQ
		return userOptional;
	}
}
