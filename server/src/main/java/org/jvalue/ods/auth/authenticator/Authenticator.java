package org.jvalue.ods.auth.authenticator;


import com.google.common.base.Optional;
import org.jvalue.ods.auth.User;

/**
 * Tries to match {@link User} objects to credentials.
 */
public interface Authenticator {

	/**
	 * @return the authenticated user if any.
	 */
	public Optional<User> authenticate(String authHeader);

}
