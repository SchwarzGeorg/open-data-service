package org.jvalue.ods.auth.authenticator;

import com.google.common.base.Optional;
import org.jvalue.ods.auth.User;

public interface RemoteAuthenticationClient {
	Optional<User> authenticate(String authHeader);
}
