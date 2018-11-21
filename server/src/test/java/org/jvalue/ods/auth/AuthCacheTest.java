package org.jvalue.ods.auth;

import org.junit.*;
import org.jvalue.ods.auth.authenticator.AuthCache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AuthCacheTest {


	CachingProvider cachingProvider;
	CacheManager cacheManager;

	Cache<String, User> userCache;
	Cache<String, String> tokenCache;
	AuthCache authCache;

	String testToken = "test";
	User testUser = new User("userId", "username", "test@user.de", Role.PUBLIC);

	@Before
	public void setUp() {
		cachingProvider = Caching.getCachingProvider();
		cacheManager = cachingProvider.getCacheManager();

		MutableConfiguration<String, User> userCacheConfig
			= new MutableConfiguration<>();
		userCache = cacheManager
			.createCache("userCache", userCacheConfig);

		MutableConfiguration<String, String> tokenCacheConfig
			= new MutableConfiguration<>();
		tokenCache = cacheManager
			.createCache("tokenCache", tokenCacheConfig);

		this.authCache = new AuthCache(userCache, tokenCache);
	}

	@After
	public void tearDown() {
		userCache.close();
		tokenCache.close();
		cacheManager.close();
		cachingProvider.close();
	}

	@Test
	public void testGetWithoutPut() {
		assertNull(authCache.getUser(testToken));
		assertNull(authCache.getToken(testUser.getId()));
	}

	@Test
	public void testGetAfterPut() {
		authCache.put(testToken, testUser);
		assertEquals(testUser, authCache.getUser(testToken));
		assertEquals(testToken, authCache.getToken(testUser.getId()));
	}

	@Test
	public void testGetAfterInvalidateByToken() {
		authCache.put(testToken, testUser);
		authCache.invalidateByToken(testToken);

		assertNull(authCache.getUser(testToken));
		assertNull(authCache.getToken(testUser.getId()));
	}

	@Test
	public void testGetAfterInvalidateByUserId() {
		authCache.put(testToken, testUser);
		authCache.invalidateByUserId(testUser.getId());

		assertNull(authCache.getUser(testToken));
		assertNull(authCache.getToken(testUser.getId()));
	}


}
