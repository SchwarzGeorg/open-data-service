package org.jvalue.ods.auth.authenticator;

import com.google.common.base.Optional;
import org.jvalue.ods.auth.AuthUser;
import org.jvalue.ods.communication.messaging.UserEvent;
import org.jvalue.ods.communication.messaging.UserEventConsumer;
import org.jvalue.ods.communication.messaging.UserEventHandler;

import javax.inject.Inject;

public class RemoteAuthenticator implements Authenticator {


	private final RemoteAuthenticationClient remoteAuthenticationClient;
	private final AuthCache cache;

	@Inject
	public RemoteAuthenticator(
		RemoteAuthenticationClient remoteAuthenticationClient,
		AuthCache authCache,
		UserEventConsumer userEventConsumer
	) {
		this.remoteAuthenticationClient = remoteAuthenticationClient;
		this.cache = authCache;

		userEventConsumer.registerEventHandler(new UserEventHandler() {
			@Override
			public void handleEvent(UserEvent userEvent) {
				if(userEvent.getType() == UserEvent.UserEventType.USER_UPDATED ||
					userEvent.getType() == UserEvent.UserEventType.USER_DELETED) {
					authCache.invalidateByUserId(userEvent.getUserId());
				}
			}
		});
	}

	@Override
	public Optional<AuthUser> authenticate(String authHeader) {
		AuthUser authUser = cache.getUser(authHeader);
		if(authUser != null) {
			// put hit
			return Optional.of(authUser);
		}

		// get authUser from UserService
		Optional<AuthUser> userOptional = remoteAuthenticationClient.authenticate(authHeader);

		if(userOptional.isPresent()) {
			// put authUser
			cache.put(authHeader, userOptional.get());
		}

		return userOptional;
	}
}
