package org.jvalue.ods.auth.authenticator;


import com.google.common.base.Optional;
import org.jvalue.ods.auth.User;

public interface Authenticator {

	/**
	 * @return the authenticated user if any.
	 */
	public Optional<User> authenticate(String authHeader);

}
