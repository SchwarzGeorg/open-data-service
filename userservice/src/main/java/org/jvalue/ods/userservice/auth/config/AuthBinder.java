package org.jvalue.ods.userservice.auth.config;


import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;
import org.jvalue.ods.userservice.auth.RestrictedTo;
import org.jvalue.ods.userservice.auth.authenticator.BasicAuthenticator;
import org.jvalue.ods.userservice.auth.authenticator.OAuthAuthenticator;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Dependency injection binder for jersey authentication
 */
public final class AuthBinder extends AbstractBinder {

	private final BasicAuthenticator basicAuthenticator;
	private final OAuthAuthenticator oAuthAuthenticator;

	@Inject
	AuthBinder(
			BasicAuthenticator basicAuthenticator,
			OAuthAuthenticator oAuthAuthenticator) {

		this.basicAuthenticator = basicAuthenticator;
		this.oAuthAuthenticator = oAuthAuthenticator;
	}


	@Override
	protected void configure() {
		bind(basicAuthenticator);
		bind(oAuthAuthenticator);

		bind(RestrictedToProvider.class)
				.to(ValueFactoryProvider.class)
				.in(Singleton.class);

		bind(RestrictedToResolver.class)
				.to(new TypeLiteral<InjectionResolver<RestrictedTo>>() { })
				.in(Singleton.class);
	}

}
