package org.jvalue.ods.userservice.auth;


import com.google.common.base.Optional;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;
import org.jvalue.ods.userservice.models.RestrictedTo;
import org.jvalue.ods.userservice.models.Role;
import org.jvalue.ods.userservice.models.User;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;


/**
 * Responsible for extracting credentials from an http request and forwarding
 * those to an {@link Authenticator}.
 */
public final class RestrictedToProvider extends AbstractValueFactoryProvider {

	private final BasicAuthenticator basicAuthenticator;
	private final OAuthAuthenticator oAuthAuthenticator;

	@Inject
	protected RestrictedToProvider(
			MultivaluedParameterExtractorProvider mpep,
			ServiceLocator locator,
			BasicAuthenticator basicAuthenticator,
			OAuthAuthenticator oAuthAuthenticator) {

		super(mpep, locator, Parameter.Source.UNKNOWN);
		this.basicAuthenticator = basicAuthenticator;
		this.oAuthAuthenticator = oAuthAuthenticator;
	}


	@Override
	protected Factory<User> createValueFactory(final Parameter parameter) {
		Class<?> classType = parameter.getRawType();
		if (classType == null || (!classType.equals(User.class))) return null;

		// factory for getting user instances based on an http request
		return new AbstractContainerRequestValueFactory<User>() {
			@Override
			public User provide() {
				RestrictedTo restrictedToAnnotation = parameter.getAnnotation(RestrictedTo.class);
				Role requiredRole = restrictedToAnnotation.value();
				boolean isAuthOptional = restrictedToAnnotation.isOptional();

				// find auth header
				List<String> headers = getContainerRequest().getRequestHeader(HttpHeaders.AUTHORIZATION);
				if (headers == null || headers.isEmpty()) return onUnauthorized(isAuthOptional);
				String authHeader = headers.get(0);

				// check authentication
				Optional<User> user = Optional.absent();
				if (authHeader.startsWith("Basic ")) user = basicAuthenticator.authenticate(authHeader);
				if (authHeader.startsWith("Bearer ")) user = oAuthAuthenticator.authenticate(authHeader);
				if (!user.isPresent()) return onUnauthorized(isAuthOptional);

				// check authorization
				if (!user.get().getRole().isMatchingRole(requiredRole)) return onUnauthorized(isAuthOptional);

				return user.get();
			}

			private User onUnauthorized(boolean isAuthOptional) {
				if (isAuthOptional) return null;
				else throw new UnauthorizedException();
			}
		};
	}

}
