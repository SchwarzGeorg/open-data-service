package org.jvalue.ods.rest;


import com.google.inject.AbstractModule;

public final class RestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DataApi.class);
		bind(NotificationApi.class);
		bind(FilterChainApi.class);
		bind(DataViewApi.class);
	}

}
