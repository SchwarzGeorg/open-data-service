package org.jvalue.ods.userservice.user;


import org.ektorp.DocumentNotFoundException;
import org.jvalue.commons.utils.Assert;
import org.jvalue.commons.utils.Log;
import org.jvalue.ods.userservice.auth.util.BasicAuthUtils;
import org.jvalue.ods.userservice.auth.credentials.BasicCredentials;
import org.jvalue.ods.userservice.auth.credentials.BasicCredentialsRepository;
import org.jvalue.ods.userservice.auth.util.OAuthUtils;
import org.jvalue.ods.userservice.communication.messaging.UserEvent;
import org.jvalue.ods.userservice.communication.messaging.UserEventProducer;
import org.jvalue.ods.userservice.rest.v1.models.BasicAuthUserDescription;
import org.jvalue.ods.userservice.rest.v1.models.OAuthUserDescription;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages lifecycle of user objects (registration, modification, deletion).
 */
@Singleton
public final class UserManager {

	private final UserRepository userRepository;

	// basic auth
	private final BasicCredentialsRepository credentialsRepository;
	private final BasicAuthUtils authenticationUtils;

	private final UserEventProducer userEventProducer;


	@Inject
	UserManager(
		UserRepository userRepository,
		BasicCredentialsRepository credentialsRepository,
		BasicAuthUtils authenticationUtils,
		UserEventProducer userEventProducer
	) {

		this.userRepository = userRepository;
		this.credentialsRepository = credentialsRepository;
		this.authenticationUtils = authenticationUtils;
		this.userEventProducer = userEventProducer;
	}


	public List<User> getAll() {
		return userRepository.getAll();
	}


	public User findById(String userId) {
		return userRepository.findById(userId);
	}


	public User findByEmail(String userEmail) {
		return userRepository.findByEmail(userEmail);
	}


	public boolean contains(String userEmail) {
		try {
			findByEmail(userEmail);
			return true;
		} catch (DocumentNotFoundException dnfe) {
			return false;
		}
	}


	public User add(BasicAuthUserDescription userDescription) {
		assertUserNotRegistered(userDescription.getEmail());
		Assert.assertTrue(
				authenticationUtils.isPartiallySecurePassword(userDescription.getPassword()),
				"password must have at least 8 characters and contain numbers");

		// store new user
		//String userId = UUID.randomUUID().toString();
		String userId = userDescription.getEmail();
		User user = new User(userId, userDescription.getName(), userDescription.getEmail(), userDescription.getRole());

		// store user
		userRepository.add(user);

		// store credentials
		byte[] salt = authenticationUtils.generateSalt();
		byte[] encryptedPassword = authenticationUtils.getEncryptedPassword(userDescription.getPassword(), salt);
		BasicCredentials credentials = new BasicCredentials(user.getId(), encryptedPassword, salt);
		credentialsRepository.add(credentials);

		Log.info("added user " + user.getId());
		//userEventProducer.publishUserEvent(new UserEvent(UserEvent.UserEventType.USER_CREATED, user));
		return user;
	}


	public User add(OAuthUserDescription userDescription, OAuthUtils.OAuthDetails oAuthDetails) {
		assertUserNotRegistered(oAuthDetails.getEmail());

		// store new user
		String userId = UUID.randomUUID().toString();
		User user = new User(userId, oAuthDetails.getGoogleUserId(), oAuthDetails.getEmail(), userDescription.getRole());

		// store user
		userRepository.add(user);

		Log.info("added user " + user.getId());
		//userEventProducer.publishUserEvent(new UserEvent(UserEvent.UserEventType.USER_CREATED, user));
		return user;
	}


	public void remove(User user) {
		userRepository.remove(user);
		try {
			BasicCredentials credentials = credentialsRepository.findById(user.getId());
			credentialsRepository.remove(credentials);
			//userEventProducer.publishUserEvent(new UserEvent(UserEvent.UserEventType.USER_DELETED, user));
		} catch (DocumentNotFoundException dnfe) {
			// user wasn't using basic auth
		}
	}


	private void assertUserNotRegistered(String email) {
		Assert.assertFalse(contains(email), "already registered user with email " + email);
	}

}
