package org.jvalue.ods.auth.authenticator;

import org.jvalue.ods.auth.AuthUser;

import javax.cache.Cache;

/**
 * Class that encapsulates the caching for authenticated users.
 * Caching in both directions (token to user and userId to token) is
 * required in order to invalidate by userId as key.
 */
public class AuthCache {

	private final Cache<String, AuthUser> userCache; // authHeader -> user
	private final Cache<String, String> tokenCache; // userId -> token

	public AuthCache(Cache<String, AuthUser> userCache, Cache<String, String> tokenCache) {
		this.userCache = userCache;
		this.tokenCache = tokenCache;
	}

	public void put(String token, AuthUser authUser) {
		userCache.put(token, authUser);
		tokenCache.put(authUser.getId(), token);
	}

	public void invalidateByToken(String token) {
		AuthUser authUser = userCache.getAndRemove(token);
		if(authUser == null) {
			return;
		}
		tokenCache.remove(authUser.getId());
	}

	public void invalidateByUserId(String userId) {
		String token = tokenCache.getAndRemove(userId);
		if(token == null) {
			return;
		}
		userCache.remove(token);
	}

	public AuthUser getUser(String token) {
		return userCache.get(token);
	}

	public String getToken(String userId) {
		return tokenCache.get(userId);
	}
}
