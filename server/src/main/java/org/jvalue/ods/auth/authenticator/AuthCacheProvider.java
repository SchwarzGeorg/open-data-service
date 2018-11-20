package org.jvalue.ods.auth.authenticator;

import javax.cache.Cache;

public class AuthCacheProvider {

	private final Cache cache;

	public AuthCacheProvider(Cache cache) {
		this.cache = cache;
	}

	public Cache getCache() {
		return cache;
	}
}
