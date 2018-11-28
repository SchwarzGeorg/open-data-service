package org.jvalue.ods.auth;

import org.junit.*;
import org.jvalue.ods.auth.authenticator.AuthCache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import static org.junit.Assert.*;

public class AuthCacheTest {


	CachingProvider cachingProvider;
	CacheManager cacheManager;

	Cache<String, AuthUser> userCache;
	Cache<String, String> tokenCache;
	AuthCache authCache;

	String testToken = "test";
	AuthUser testAuthUser = new AuthUser("userId", "username", "test@user.de", Role.PUBLIC);

	@Before
	public void setUp() {
		cachingProvider = Caching.getCachingProvider();
		cacheManager = cachingProvider.getCacheManager();

		MutableConfiguration<String, AuthUser> userCacheConfig
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
		assertNull(authCache.getToken(testAuthUser.getId()));
	}

	@Test
	public void testGetAfterPut() {
		authCache.put(testToken, testAuthUser);
		assertEquals(testAuthUser, authCache.getUser(testToken));
		assertEquals(testToken, authCache.getToken(testAuthUser.getId()));
	}

	@Test
	public void testGetAfterInvalidateByToken() {
		authCache.put(testToken, testAuthUser);
		authCache.invalidateByToken(testToken);

		assertNull(authCache.getUser(testToken));
		assertNull(authCache.getToken(testAuthUser.getId()));
	}

	@Test
	public void testGetAfterInvalidateByUserId() {
		authCache.put(testToken, testAuthUser);
		authCache.invalidateByUserId(testAuthUser.getId());

		assertNull(authCache.getUser(testToken));
		assertNull(authCache.getToken(testAuthUser.getId()));
	}


}
