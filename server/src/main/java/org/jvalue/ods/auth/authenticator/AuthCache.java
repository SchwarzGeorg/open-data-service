package org.jvalue.ods.auth.authenticator;

import org.jvalue.ods.auth.User;

import javax.cache.Cache;

/**
 * Class that encapsulates the caching for authenticated users.
 * Caching in both directions (token to user and userId to token) is
 * required in order to invalidate by userId as key.
 */
public class AuthCache {

	private final Cache<String, User> userCache; // authHeader -> user
	private final Cache<String, String> tokenCache; // userId -> token

	public AuthCache(Cache<String, User> userCache, Cache<String, String> tokenCache) {
		this.userCache = userCache;
		this.tokenCache = tokenCache;
	}

	public void put(String token, User user) {
		userCache.put(token, user);
		tokenCache.put(user.getId(), token);
	}

	public void invalidateByToken(String token) {
		User user = userCache.getAndRemove(token);
		if(user == null) {
			return;
		}
		tokenCache.remove(user.getId());
	}

	public void invalidateByUserId(String userId) {
		String token = tokenCache.getAndRemove(userId);
		if(token == null) {
			return;
		}
		userCache.remove(token);
	}

	public User getUser(String token) {
		return userCache.get(token);
	}

	public String getToken(String userId) {
		return tokenCache.get(userId);
	}
}
