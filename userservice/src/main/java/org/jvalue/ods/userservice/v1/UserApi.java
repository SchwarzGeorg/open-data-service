package org.jvalue.ods.userservice.v1;

import com.google.common.base.Optional;
import org.ektorp.DocumentNotFoundException;
import org.jvalue.ods.userservice.auth.*;
import org.jvalue.commons.rest.RestUtils;
import org.jvalue.ods.userservice.auth.authenticator.BasicAuthenticator;
import org.jvalue.ods.userservice.auth.exception.UnauthorizedException;
import org.jvalue.ods.userservice.auth.util.BasicAuthUtils;
import org.jvalue.ods.userservice.auth.util.OAuthUtils;
import org.jvalue.ods.userservice.v1.models.*;
import org.jvalue.ods.userservice.user.Role;
import org.jvalue.ods.userservice.user.User;
import org.jvalue.ods.userservice.user.UserManager;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path(AbstractApi.VERSION + "/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class UserApi extends AbstractApi {

	private final UserManager userManager;
	private final BasicAuthenticator basicAuthenticator;
	private final BasicAuthUtils basicAuthUtils;
	private final OAuthUtils oAuthUtils;


	@Inject
	public UserApi(UserManager userManager, BasicAuthenticator basicAuthenticator, BasicAuthUtils basicAuthUtils, OAuthUtils oAuthUtils) {
		this.userManager = userManager;
		this.basicAuthenticator = basicAuthenticator;
		this.basicAuthUtils = basicAuthUtils;
		this.oAuthUtils = oAuthUtils;
	}

	@GET
	public List<User> getAllUsers(@RestrictedTo(Role.ADMIN) User user) {
		return userManager.getAll();
	}


	@PUT
	@Path("/me")
	public User addUser(@RestrictedTo(value = Role.ADMIN, isOptional = true) User user, AbstractUserDescription userDescription) {
		// check for valid role (only admins can add admins)
		if (userDescription.getRole().equals(Role.ADMIN) && user == null) throw new UnauthorizedException("missing admin privileges");

		if (userDescription instanceof BasicAuthUserDescription) return addUser((BasicAuthUserDescription) userDescription);
		else return addUser((OAuthUserDescription) userDescription);
	}


	@GET
	@Path("/{userId}")
	public User getUser(@RestrictedTo(Role.PUBLIC) User user, @PathParam("userId") String userId) {
		if (!userId.equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) throw new UnauthorizedException();
		return userManager.findById(userId);
	}


	@GET
	@Path("/me")
	public User getUser(@RestrictedTo(Role.PUBLIC) User user) {
		return user;
	}


	@DELETE
	@Path("/{userId}")
	public void removeUser(@RestrictedTo(Role.PUBLIC) User user, @PathParam("userId") String userId) {
		if (!userId.equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) throw new UnauthorizedException();
		userManager.remove(userManager.findById(userId));
	}


	private User addUser(BasicAuthUserDescription userDescription) {
		// check for partially secure password
		if (!basicAuthUtils.isPartiallySecurePassword(userDescription.getPassword()))
			throw RestUtils.createJsonFormattedException("password must be at least 8 characters and contain numbers", 400);

		// if already registered, simply return user
		Optional<User> registeredUser = basicAuthenticator.authenticate(userDescription.getEmail(), userDescription.getPassword());
		if (registeredUser.isPresent()) return registeredUser.get();

		// else register new user
		assertNotRegistered(userDescription.getEmail());
		return userManager.add(userDescription);
	}


	private User addUser(OAuthUserDescription userDescription) {
		// check for valid auth token
		Optional<OAuthUtils.OAuthDetails> tokenDetails = oAuthUtils.checkAuthHeader(userDescription.getAuthToken());
		if (!tokenDetails.isPresent()) throw new UnauthorizedException();

		// if already registered simply return user
		Optional<User> registeredUser = findUserByEmail(tokenDetails.get().getEmail());
		if (registeredUser.isPresent()) return registeredUser.get();

		// else register new user
		assertNotRegistered(tokenDetails.get().getEmail());
		return userManager.add(userDescription, tokenDetails.get());
	}


	private void assertNotRegistered(String email) {
		if (userManager.contains(email)) {
			System.out.println("already registered");
			throw RestUtils.createJsonFormattedException("email already registered", 409);
		}
	}

	private Optional<User> findUserByEmail(String email) {
		try {
			return Optional.of(userManager.findByEmail(email));
		} catch (DocumentNotFoundException dnfe) {
			return Optional.absent();
		}
	}
}

