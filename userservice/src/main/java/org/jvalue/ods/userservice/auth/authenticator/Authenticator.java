package org.jvalue.ods.userservice.auth.authenticator;


import com.google.common.base.Optional;
import org.jvalue.ods.userservice.user.User;

/**
 * Tries to match {@link User} objects to credentials.
 */
public interface Authenticator {

	/**
	 * @return the authenticated user if any.
	 */
	public Optional<User> authenticate(String authHeader);

}
