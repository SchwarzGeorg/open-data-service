package org.jvalue.ods.auth.authenticator;


import com.google.common.base.Optional;
import org.jvalue.ods.auth.AuthUser;

public interface Authenticator {

	/**
	 * @return the authenticated user if any.
	 */
	Optional<AuthUser> authenticate(String authHeader);

}
