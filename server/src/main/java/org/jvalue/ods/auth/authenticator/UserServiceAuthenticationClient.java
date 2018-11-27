package org.jvalue.ods.auth.authenticator;

import com.google.common.base.Optional;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.jvalue.ods.auth.User;
import org.jvalue.ods.auth.config.AuthConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserServiceAuthenticationClient implements RemoteAuthenticationClient {

	public final String userServicePath;

	private RetryPolicy retryPolicy;

	public UserServiceAuthenticationClient(
		AuthConfig authConfig
	) {
		userServicePath = authConfig.getUserServiceUrl();

		retryPolicy = new RetryPolicy()
			.retryOn(List.of(ConnectException.class, SocketException.class))
			.withDelay(1, TimeUnit.SECONDS)
			.withMaxRetries(3);
	}

	@Override
	public Optional<User> authenticate(String authHeader) {
		return Failsafe.with(retryPolicy).get(() -> requestUser(authHeader));
	}

	private Optional<User> requestUser(String authHeader) {
		Client client = ClientBuilder.newClient();
		return Optional.of(
			client
				.target(userServicePath + "/users/me")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, authHeader)
				.get(User.class)
		);
	}
}
