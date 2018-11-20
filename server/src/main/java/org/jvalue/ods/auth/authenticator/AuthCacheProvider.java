package org.jvalue.ods.auth.authenticator;

import org.jvalue.ods.auth.User;

import javax.cache.Cache;

public class AuthCacheProvider {

	private final Cache<String, User> cache;

	public AuthCacheProvider(Cache<String, User> cache) {
		this.cache = cache;
	}

	public Cache<String, User> getCache() {
		return cache;
	}
}
