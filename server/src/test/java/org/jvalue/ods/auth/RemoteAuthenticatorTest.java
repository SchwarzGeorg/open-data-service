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


import javax.cache.Cache;

import static org.junit.Assert.*;

@RunWith(JMockit.class)
public class RemoteAuthenticatorTest {


	@Mocked
	private RemoteAuthenticationClient remoteAuthenticationClient;

	@Mocked
	private AuthCache authCache;

	User testUser = new User(
		"1", "Test User", "test@testUser.de", Role.ADMIN
	);

	@Test
	public void testCachedUserOnlyFetchedOnce() {
		String authHeader = "authHeader";
		new Expectations() {{
			remoteAuthenticationClient.authenticate(anyString);
			result = Optional.of(testUser);

			authCache.getUser(authHeader);
			returns(null, testUser, testUser);
		}};


		RemoteAuthenticator remoteAuthenticator =
			new RemoteAuthenticator(remoteAuthenticationClient, authCache);
		// first call
		Optional<User> user = remoteAuthenticator.authenticate(authHeader);
		assertIsTestUser(user);
		// second call
		user = remoteAuthenticator.authenticate(authHeader);
		assertIsTestUser(user);

		new Verifications() {{
			remoteAuthenticationClient.authenticate(authHeader);
			times = 1;

			authCache.put(authHeader, testUser);
			times = 1;
		}};
	}

	private void assertIsTestUser(Optional<User> user) {
		assertTrue(user.isPresent());
		assertEquals(user.get(), this.testUser);
	}

}
