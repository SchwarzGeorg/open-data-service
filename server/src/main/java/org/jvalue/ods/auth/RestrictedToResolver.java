package org.jvalue.ods.auth;


import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.jvalue.commons.auth.RestrictedTo;


/**
 * Determines where the {@link RestrictedTo} annotation can
 * appear (on parameters).
 */
public final class RestrictedToResolver extends ParamInjectionResolver<RestrictedTo> {

	public RestrictedToResolver() {
		super(RestrictedToProvider.class);
	}

}
