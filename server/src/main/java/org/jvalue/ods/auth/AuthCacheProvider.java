package org.jvalue.ods.auth;

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
