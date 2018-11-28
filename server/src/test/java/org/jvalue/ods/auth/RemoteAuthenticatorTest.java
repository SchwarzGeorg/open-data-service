package org.jvalue.ods.auth;

import com.google.common.base.Optional;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvalue.ods.auth.authenticator.AuthCache;
import org.jvalue.ods.auth.authenticator.RemoteAuthenticationClient;
import org.jvalue.ods.auth.authenticator.RemoteAuthenticator;
import org.jvalue.ods.communication.messaging.UserEventConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JMockit.class)
public class RemoteAuthenticatorTest {


	@Mocked
	private RemoteAuthenticationClient remoteAuthenticationClient;

	@Mocked
	private AuthCache authCache;

	@Mocked
	private UserEventConsumer userEventConsumer;


	AuthUser testAuthUser = new AuthUser(
		"1", "Test AuthUser", "test@testAuthUser.de", Role.ADMIN
	);

	@Test
	public void testCachedUserOnlyFetchedOnce() {
		String authHeader = "authHeader";
		new Expectations() {{
			remoteAuthenticationClient.authenticate(anyString);
			result = Optional.of(testAuthUser);

			authCache.getUser(authHeader);
			returns(null, testAuthUser, testAuthUser);
		}};


		RemoteAuthenticator remoteAuthenticator =
			new RemoteAuthenticator(remoteAuthenticationClient, authCache, userEventConsumer);
		// first call
		Optional<AuthUser> user = remoteAuthenticator.authenticate(authHeader);
		assertIsTestUser(user);
		// second call
		user = remoteAuthenticator.authenticate(authHeader);
		assertIsTestUser(user);

		new Verifications() {{
			remoteAuthenticationClient.authenticate(authHeader);
			times = 1;

			authCache.put(authHeader, testAuthUser);
			times = 1;
		}};
	}

	private void assertIsTestUser(Optional<AuthUser> user) {
		assertTrue(user.isPresent());
		assertEquals(user.get(), this.testAuthUser);
	}

}
