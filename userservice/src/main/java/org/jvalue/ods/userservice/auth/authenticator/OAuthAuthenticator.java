package org.jvalue.ods.userservice.auth.authenticator;


import com.google.common.base.Optional;
import org.jvalue.ods.userservice.auth.util.OAuthUtils;
import org.jvalue.ods.userservice.user.User;
import org.jvalue.ods.userservice.user.UserManager;

import javax.inject.Inject;

/**
 * Performs authentication using Google OAuth.
 */
public class OAuthAuthenticator implements Authenticator {

	private final UserManager userManager;
	private final OAuthUtils authUtils;

	@Inject
	OAuthAuthenticator(UserManager userManager, OAuthUtils authUtils) {
		this.userManager = userManager;
		this.authUtils = authUtils;
	}


	@Override
	public Optional<User> authenticate(String tokenString) {
		Optional<OAuthUtils.OAuthDetails> authDetails = authUtils.checkAuthHeader(tokenString.replaceFirst("Bearer ", ""));
		if (!authDetails.isPresent()) return Optional.absent();
		if (!userManager.contains(authDetails.get().getEmail())) return Optional.absent();
		return Optional.of(userManager.findByEmail(authDetails.get().getEmail()));
	}

}
