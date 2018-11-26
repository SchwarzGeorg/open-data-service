package org.jvalue.ods.auth.config;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;
import org.jvalue.ods.auth.RestrictedTo;
import org.jvalue.ods.auth.authenticator.RemoteAuthenticator;

import javax.inject.Inject;
import javax.inject.Singleton;

public class AuthBinder extends AbstractBinder {

	private final RemoteAuthenticator remoteAuthenticator;

	@Inject
	AuthBinder(RemoteAuthenticator remoteAuthenticator) {
		this.remoteAuthenticator = remoteAuthenticator;
	}


	@Override
	protected void configure() {
		bind(remoteAuthenticator);

		bind(RestrictedToProvider.class)
			.to(ValueFactoryProvider.class)
			.in(Singleton.class);

		bind(RestrictedToResolver.class)
			.to(new TypeLiteral<InjectionResolver<RestrictedTo>>() { })
			.in(Singleton.class);
	}

}
