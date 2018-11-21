package org.jvalue.ods.main;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hubspot.jackson.jaxrs.PropertyFilteringMessageBodyWriter;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.GenericConstraintDef;
import org.jvalue.commons.couchdb.rest.DbExceptionMapper;
import org.jvalue.commons.rest.JsonExceptionMapper;
import org.jvalue.commons.rest.NotFoundExceptionMapper;
import org.jvalue.commons.utils.HttpServiceCheck;
import org.jvalue.ods.admin.monitoring.DbHealthCheck;
import org.jvalue.ods.admin.monitoring.MonitoringModule;
import org.jvalue.ods.admin.rest.AdminFilterChainApi;
import org.jvalue.ods.api.processors.ProcessorReferenceChainDescription;
import org.jvalue.ods.auth.config.AuthBinder;
import org.jvalue.ods.auth.exception.UnauthorizedExceptionMapper;
import org.jvalue.ods.data.DataModule;
import org.jvalue.ods.data.DataSourceManager;
import org.jvalue.ods.db.DbModule;
import org.jvalue.ods.notifications.NotificationsModule;
import org.jvalue.ods.pegelalarm.CepsClientHealthCheck;
import org.jvalue.ods.pegelalarm.DataHealthCheck;
import org.jvalue.ods.pegelalarm.DataSourceHealthCheck;
import org.jvalue.ods.pegelalarm.FilterChainHealthCheck;
import org.jvalue.ods.pegelalarm.PegelOnlineHealthCheck;
import org.jvalue.ods.processor.ProcessorModule;
import org.jvalue.ods.processor.reference.ValidChainReference;
import org.jvalue.ods.transformation.DataTransformationModule;
import org.jvalue.ods.rest.v1.DataApi;
import org.jvalue.ods.rest.v1.DataSourceApi;
import org.jvalue.ods.rest.v1.DataViewApi;
import org.jvalue.ods.rest.v1.NotificationApi;
import org.jvalue.ods.rest.v1.PluginApi;
import org.jvalue.ods.rest.v1.ProcessorChainApi;
import org.jvalue.ods.rest.v1.ProcessorSpecificationApi;
import org.jvalue.ods.auth.AuthModule;
import org.jvalue.ods.rest.v1.VersionApi;
import org.jvalue.ods.utils.GuiceConstraintValidatorFactory;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.validation.Validation;
import javax.ws.rs.core.Context;

import io.dropwizard.Application;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.setup.JerseyContainerHolder;
import io.dropwizard.setup.Environment;

public final class OdsApplication extends Application<OdsConfig> {


	public static void main(String[] args) throws Exception {
		new OdsApplication().run(args);
	}


	@Override
	public String getName() {
		return "Open Data Service";
	}


	@Override
	@Context
	public void run(OdsConfig configuration, Environment environment) {
		// wait until dependent services are up
		assertCouchDbIsReady(configuration.getCouchDb().getUrl());
		assertRabbitMqIsReady(configuration.getMessaging().getBrokerUrl());
		assertUserServiceIsReady(configuration.getAuth().getUserServiceHealthcheckUrl());

		// register modules
		Injector injector = Guice.createInjector(
				new MonitoringModule(environment.metrics()),
				new ConfigModule(configuration),
				new ProcessorModule(),
				new DbModule(configuration.getCouchDb()),
				new NotificationsModule(),
				new DataModule(),
				new AuthModule(configuration.getAuth()),
				new DataTransformationModule());

		// start data grabbing
		environment.lifecycle().manage(injector.getInstance(DataSourceManager.class));
		environment.jersey().getResourceConfig().register(MultiPartFeature.class);
		environment.jersey().getResourceConfig().register(injector.getInstance(AuthBinder.class));
		environment.jersey().register(injector.getInstance(DataSourceApi.class));
		environment.jersey().register(injector.getInstance(DataApi.class));
		environment.jersey().register(injector.getInstance(ProcessorChainApi.class));
		environment.jersey().register(injector.getInstance(DataViewApi.class));
		environment.jersey().register(injector.getInstance(NotificationApi.class));
		environment.jersey().register(injector.getInstance(PluginApi.class));
		environment.jersey().register(injector.getInstance(ProcessorSpecificationApi.class));
		environment.jersey().register(injector.getInstance(VersionApi.class));
		environment.jersey().register(PropertyFilteringMessageBodyWriter.class);
		environment.jersey().register(new DbExceptionMapper());
		environment.jersey().register(new JsonExceptionMapper());
		environment.jersey().register(new UnauthorizedExceptionMapper());
		environment.jersey().register(new NotFoundExceptionMapper());

		// v2
        environment.jersey().register(injector.getInstance(org.jvalue.ods.rest.v2.DataSourceApi.class));

		// setup health checks
		environment.healthChecks().register(DbHealthCheck.class.getSimpleName(), injector.getInstance(DbHealthCheck.class));
		environment.healthChecks().register(PegelOnlineHealthCheck.class.getSimpleName(), injector.getInstance(PegelOnlineHealthCheck.class));
		environment.healthChecks().register(DataSourceHealthCheck.class.getSimpleName(), injector.getInstance(DataSourceHealthCheck.class));
		environment.healthChecks().register(FilterChainHealthCheck.class.getSimpleName(), injector.getInstance(FilterChainHealthCheck.class));
		environment.healthChecks().register(DataHealthCheck.class.getSimpleName(), injector.getInstance(DataHealthCheck.class));
		environment.healthChecks().register(CepsClientHealthCheck.class.getSimpleName(), injector.getInstance(CepsClientHealthCheck.class));

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
		jerseyConfig.register(injector.getInstance(AdminFilterChainApi.class));
		environment.admin().addServlet("admin resources", jerseyContainerHolder.getContainer()).addMapping("/admin/*");

		// setup validation for external classes
		HibernateValidatorConfiguration validatorContext = Validation.byProvider(HibernateValidator.class).configure();
		ConstraintMapping mapping = validatorContext.createConstraintMapping();
		mapping.type(ProcessorReferenceChainDescription.class).constraint(new GenericConstraintDef<>(ValidChainReference.class));

		// setup Guice DI for hibernate validator
		environment.setValidator(validatorContext.addMapping(mapping)
				.constraintValidatorFactory(new GuiceConstraintValidatorFactory(injector))
				.buildValidatorFactory()
				.getValidator());
	}

	private void assertCouchDbIsReady(String couchDbUrl) {
		if (!HttpServiceCheck.check(couchDbUrl)) {
			throw new RuntimeException("CouchDB service is not ready [" + couchDbUrl+ "]");
		}

	}

	private void assertUserServiceIsReady(String userServiceUrl) {
		if (!HttpServiceCheck.check(userServiceUrl)) {
			throw new RuntimeException("UserService is not ready [" + userServiceUrl+ "]");
		}

	}

	private void assertRabbitMqIsReady(String rabbitMqUrl) {
		if (!HttpServiceCheck.check(rabbitMqUrl)) {
			throw new RuntimeException("RabbitMQ is not ready [" + rabbitMqUrl+ "]");
		}
	}

}
