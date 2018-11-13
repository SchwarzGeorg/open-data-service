package org.jvalue.ods.userservice.auth;


import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.jvalue.ods.userservice.models.RestrictedTo;

/**
 * Determines where the {@link RestrictedTo} annotation can
 * appear (on parameters).
 */
public final class RestrictedToResolver extends ParamInjectionResolver<RestrictedTo> {

	public RestrictedToResolver() {
		super(RestrictedToProvider.class);
	}

}
