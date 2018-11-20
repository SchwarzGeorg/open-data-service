package org.jvalue.ods.auth.authenticator;

import com.google.common.base.Optional;
import org.jvalue.ods.auth.User;

import javax.cache.Cache;
import javax.inject.Inject;

public class RemoteAuthenticator implements Authenticator {


	private final RemoteAuthenticationClient remoteAuthenticationClient;
	private final Cache cache;

	@Inject
	public RemoteAuthenticator(
		RemoteAuthenticationClient remoteAuthenticationClient,
		AuthCacheProvider authCacheProvider
	) {
		this.remoteAuthenticationClient = remoteAuthenticationClient;
		this.cache = authCacheProvider.getCache();
	}

	@Override
	public Optional<User> authenticate(String authHeader) {
		User user = (User) cache.get(authHeader);
		if(user != null) {
			// cache hit
			return Optional.of(user);
		}

		// get user from UserService
		Optional<User> userOptional = remoteAuthenticationClient.authenticate(authHeader);

		if(userOptional.isPresent()) {
			// cache user
			cache.put(authHeader, userOptional.get());
		}

		// TODO: react on changes via RabbitMQ
		return userOptional;
	}
}
