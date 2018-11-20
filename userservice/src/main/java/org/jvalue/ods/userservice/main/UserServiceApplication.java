package org.jvalue.ods.userservice.main;


import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.jvalue.commons.couchdb.rest.DbExceptionMapper;
import org.jvalue.commons.rest.JsonExceptionMapper;
import org.jvalue.commons.rest.NotFoundExceptionMapper;
import org.jvalue.commons.utils.HttpServiceCheck;
import org.jvalue.ods.userservice.auth.config.AuthBinder;
import org.jvalue.ods.userservice.auth.AuthModule;
import org.jvalue.ods.userservice.auth.exception.UnauthorizedExceptionMapper;
import org.jvalue.ods.userservice.user.UserManager;
import org.jvalue.ods.userservice.db.DbHealthCheck;
import org.jvalue.ods.userservice.db.DbModule;
import org.jvalue.ods.userservice.rest.v1.models.BasicAuthUserDescription;
import org.jvalue.ods.userservice.utils.GuiceConstraintValidatorFactory;
import org.jvalue.ods.userservice.rest.v1.UserApi;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.validation.Validation;
import javax.ws.rs.core.Context;
import java.util.EnumSet;
import java.util.List;

public final class UserServiceApplication extends Application<UserServiceConfig> {


	public static void main(String[] args) throws Exception {
		new UserServiceApplication().run(args);
	}


	@Override
	public String getName() {
		return "ODS UserService";
	}


	@Override
	@Context
	public void run(UserServiceConfig configuration, Environment environment) {
		// wait until db is up
		assertCouchDbIsReady(configuration.getCouchDb().getUrl());

		// register modules
		Injector injector = Guice.createInjector(
				new DbModule(configuration.getCouchDb()),
				new AuthModule(configuration.getAuth())
			);

		// start data grabbing
		environment.jersey().getResourceConfig().register(injector.getInstance(AuthBinder.class));
		environment.jersey().register(injector.getInstance(UserApi.class));

		environment.jersey().register(new DbExceptionMapper());
		environment.jersey().register(new JsonExceptionMapper());
		environment.jersey().register(new UnauthorizedExceptionMapper());
		environment.jersey().register(new NotFoundExceptionMapper());

		// setup users
		setupDefaultUsers(injector.getInstance(UserManager.class), configuration.getAuth().getUsers());

		// setup health checks
		environment.healthChecks().register(DbHealthCheck.class.getSimpleName(), injector.getInstance(DbHealthCheck.class));


		// setup Cross-Origin Resource Sharing (CORS)
		final FilterRegistration.Dynamic corsFilter =
			environment.servlets().addFilter("CORS", CrossOriginFilter.class);

		corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
			"X-Requested-With,Content-Type,Accept,Origin,Authorization");
		corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
		corsFilter.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
		corsFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

		// setup validation for external classes
		HibernateValidatorConfiguration validatorContext = Validation.byProvider(HibernateValidator.class).configure();
		ConstraintMapping mapping = validatorContext.createConstraintMapping();
		//TODO

		// setup Guice DI for hibernate validator
		environment.setValidator(validatorContext.addMapping(mapping)
				.constraintValidatorFactory(new GuiceConstraintValidatorFactory(injector))
				.buildValidatorFactory()
				.getValidator());
	}

	private void setupDefaultUsers(UserManager userManager, List<BasicAuthUserDescription> userList) {
		for (BasicAuthUserDescription user : userList) {
			if (!userManager.contains(user.getEmail())) userManager.add(user);
		}
	}

	private void assertCouchDbIsReady(String couchDbUrl) {
		if (!HttpServiceCheck.check(couchDbUrl)) {
			throw new RuntimeException("CouchDB service is not ready [" + couchDbUrl+ "]");
		}

	}
}
