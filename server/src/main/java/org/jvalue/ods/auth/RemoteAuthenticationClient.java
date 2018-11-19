package org.jvalue.ods.auth;

import com.google.common.base.Optional;
import org.jvalue.commons.auth.User;

public interface RemoteAuthenticationClient {
	public Optional<User> authenticate(String authHeader);
}
