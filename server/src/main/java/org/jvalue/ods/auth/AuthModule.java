package org.jvalue.ods.auth;

import com.google.inject.AbstractModule;
import org.jvalue.commons.auth.User;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;


public final class AuthModule extends AbstractModule {

	private static final long CACHE_EXPIRES_AFTER_SECONDS = 600;
	private final AuthConfig authConfig;

	public AuthModule(AuthConfig authConfig) {
		this.authConfig = authConfig;
	}

	@Override
	protected void configure() {
		bind(AuthConfig.class).toInstance(authConfig);
		bind(RemoteAuthenticationClient.class).toInstance(new UserServiceAuthenticationClient(authConfig));

		// create and bind user cache
		CachingProvider cachingProvider = Caching.getCachingProvider();
		CacheManager cacheManager = cachingProvider.getCacheManager();
		MutableConfiguration<String, User> config
			= new MutableConfiguration<>();
		config.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(
			new Duration(TimeUnit.SECONDS, CACHE_EXPIRES_AFTER_SECONDS)));
		Cache<String, User> cache = cacheManager
			.createCache("userCache", config);

		bind(AuthCacheProvider.class).toInstance(new AuthCacheProvider(cache));
	}

}
