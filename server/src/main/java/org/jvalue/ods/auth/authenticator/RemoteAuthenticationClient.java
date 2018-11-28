package org.jvalue.ods.auth.authenticator;

import com.google.common.base.Optional;
import org.jvalue.ods.auth.AuthUser;

public interface RemoteAuthenticationClient {
	Optional<AuthUser> authenticate(String authHeader);
}
