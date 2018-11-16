package org.jvalue.ods.userservice.main;


import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.setup.JerseyContainerHolder;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.jvalue.commons.couchdb.rest.DbExceptionMapper;
import org.jvalue.commons.rest.JsonExceptionMapper;
import org.jvalue.commons.rest.NotFoundExceptionMapper;
import org.jvalue.ods.userservice.auth.AuthBinder;
import org.jvalue.ods.userservice.auth.AuthModule;
import org.jvalue.ods.userservice.auth.UnauthorizedExceptionMapper;
import org.jvalue.ods.userservice.db.DbModule;
import org.jvalue.ods.userservice.utils.GuiceConstraintValidatorFactory;
import org.jvalue.ods.userservice.v1.UserApi;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.validation.Validation;
import javax.ws.rs.core.Context;
import java.util.EnumSet;

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

		Injector injector = Guice.createInjector(
				new DbModule(configuration.getCouchDb()),
				new AuthModule(configuration.getAuth())
				//TODO
			);

		// start data grabbing
		// TODO
		environment.jersey().getResourceConfig().register(injector.getInstance(AuthBinder.class));
		environment.jersey().register(injector.getInstance(UserApi.class));

		environment.jersey().register(new DbExceptionMapper());
		environment.jersey().register(new JsonExceptionMapper());
		environment.jersey().register(new UnauthorizedExceptionMapper());
		environment.jersey().register(new NotFoundExceptionMapper());

		// setup users
		// TODO

		// setup health checks
		// TODO

		// setup Cross-Origin Resource Sharing (CORS)
		final FilterRegistration.Dynamic corsFilter =
			environment.servlets().addFilter("CORS", CrossOriginFilter.class);

		corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
			"X-Requested-With,Content-Type,Accept,Origin,Authorization");
		corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
		corsFilter.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
		corsFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

		// configure administration
		DropwizardResourceConfig jerseyConfig = new DropwizardResourceConfig(environment.metrics());
		JerseyContainerHolder jerseyContainerHolder = new JerseyContainerHolder(new ServletContainer(jerseyConfig));
		//TODO

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
}
