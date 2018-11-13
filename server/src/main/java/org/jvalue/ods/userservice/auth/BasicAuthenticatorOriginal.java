package org.jvalue.ods.userservice.auth;


import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import org.ektorp.DocumentNotFoundException;
import org.jvalue.commons.auth.Authenticator;
import org.jvalue.commons.auth.BasicAuthUtils;
import org.jvalue.commons.auth.BasicCredentials;
import org.jvalue.commons.auth.BasicCredentialsRepository;
import org.jvalue.commons.auth.*;
import org.jvalue.commons.auth.UserManager;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

// TODO: delete after extraction of UserService

/**
 * Handles basic (username + password) authentication.
 */
public final class BasicAuthenticatorOriginal implements Authenticator {

	private final UserManager userManager;
	private final BasicCredentialsRepository credentialsRepository;
	private final BasicAuthUtils authenticationUtils;

	//TODO: remove public adter extraction of UserService
	@Inject
	public BasicAuthenticatorOriginal(
			UserManager userManager,
			BasicCredentialsRepository credentialsRepository,
			BasicAuthUtils authenticationUtils) {

		this.userManager = userManager;
		this.credentialsRepository = credentialsRepository;
		this.authenticationUtils  = authenticationUtils;
	}


	@Override
	public Optional<User> authenticate(String authHeader) {
		// decode credentials
		String token = new String(BaseEncoding.base64().decode(
				authHeader.replaceFirst("Basic ", "")),
				StandardCharsets.UTF_8);

		int colon = token.indexOf(':');
		if (colon < 0) return Optional.absent();
		String email = token.substring(0, colon);
		String password = token.substring(colon + 1);
		return authenticate(email,password);
	}


	public Optional<User> authenticate(String email, String password) {
		try {
			User user = userManager.findByEmail(email);
			BasicCredentials credentials = credentialsRepository.findById(user.getId());
			if (authenticationUtils.checkPassword(password, credentials)) {
				return Optional.of(user);
			} else {
				return Optional.absent();
			}

		} catch (DocumentNotFoundException dnfe) {
			return Optional.absent();
		}
	}

}
