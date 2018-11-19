package org.jvalue.ods.auth;

import com.google.inject.AbstractModule;


public final class AuthModule extends AbstractModule {

	private final AuthConfig authConfig;

	public AuthModule(AuthConfig authConfig) {
		this.authConfig = authConfig;
	}

	@Override
	protected void configure() {
		bind(AuthConfig.class).toInstance(authConfig);
		bind(RemoteAuthenticationClient.class).toInstance(new UserServiceAuthenticationClient(authConfig));
	}

}
