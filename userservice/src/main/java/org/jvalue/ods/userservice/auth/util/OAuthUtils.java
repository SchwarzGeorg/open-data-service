package org.jvalue.ods.userservice.auth.util;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.base.Optional;

import org.jvalue.commons.utils.Log;
import org.jvalue.ods.userservice.auth.config.AuthConfig;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.inject.Inject;

/**
 * Helper methods for Google OAuth.
 */
public final class OAuthUtils {

	private final String googleOAuthWebClientId;
	private final List<String> googleAuthClientIds;
	private final JsonFactory jsonFactory = new JacksonFactory();
	private final GoogleIdTokenVerifier tokenVerifier = new GoogleIdTokenVerifier(
			new NetHttpTransport(),
			jsonFactory);


	@Inject
	OAuthUtils(
			AuthConfig authConfig) {

		this.googleOAuthWebClientId = authConfig.getGoogleOAuthWebClientId();
		this.googleAuthClientIds = authConfig.getGoogleOAuthClientIds();
	}


	public Optional<OAuthDetails> checkAuthHeader(String authToken) {
		try {
			GoogleIdToken token = GoogleIdToken.parse(jsonFactory, authToken);

			if (tokenVerifier.verify(token)) {
				GoogleIdToken.Payload payload = token.getPayload();

				if (!payload.getAudience().equals(googleOAuthWebClientId)) {
					return Optional.absent();
				}

				if (!googleAuthClientIds.contains(payload.getAuthorizedParty())) return Optional.absent();

				return Optional.of(new OAuthDetails(payload.getSubject(), payload.getEmail()));
			}

		} catch (GeneralSecurityException | IOException e) {
			Log.debug("oauth failed", e);
		}
		return Optional.absent();
	}


	public static final class OAuthDetails {

		private final String googleUserId;
		private final String email;


		public OAuthDetails(String googleUserId, String email) {
			this.googleUserId = googleUserId;
			this.email = email;
		}


		public String getGoogleUserId() {
			return googleUserId;
		}


		public String getEmail() {
			return email;
		}

	}

}
