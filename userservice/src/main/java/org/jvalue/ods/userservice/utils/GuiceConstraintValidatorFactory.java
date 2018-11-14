package org.jvalue.ods.userservice.utils;


import com.google.inject.Injector;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

/**
 * Use Guice DI for creating {@link ConstraintValidator} instances.
 */
public final class GuiceConstraintValidatorFactory implements ConstraintValidatorFactory {

	private final Injector injector;

	public GuiceConstraintValidatorFactory(Injector injector) {
		this.injector = injector;
	}


	@Override
	public <T extends ConstraintValidator<?,?>> T getInstance(Class<T> validatorClass) {
		return injector.getInstance(validatorClass);
	}


	@Override
	public void releaseInstance(ConstraintValidator<?,?> instance) {
		// not using any caching at the moment
	}

}
