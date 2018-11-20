package org.jvalue.ods.userservice.rest.v1.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.jvalue.ods.userservice.user.Role;

/**
 * A user which can be registered for oauth.
 */
public final class OAuthUserDescription extends AbstractUserDescription {

	static final String TYPE = "oauth";

	private final String authToken;

	@JsonCreator
	public OAuthUserDescription(
			@JsonProperty("role") Role role,
			@JsonProperty("authToken") String authToken) {

		super(TYPE, role);
		this.authToken = authToken;
	}


	public String getAuthToken() {
		return authToken;
	}


	@Override
	public boolean equals(Object other) {
		if (!super.equals(other) || !(other instanceof OAuthUserDescription)) return false;
		OAuthUserDescription user = (OAuthUserDescription) other;
		return Objects.equal(authToken, user.authToken);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), authToken);
	}

}
