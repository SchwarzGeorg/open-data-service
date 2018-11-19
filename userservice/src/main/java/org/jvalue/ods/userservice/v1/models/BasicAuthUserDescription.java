package org.jvalue.ods.userservice.v1.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.jvalue.ods.userservice.user.Role;

import javax.validation.constraints.NotNull;

/**
 * A user which can be registered for basic auth.
 */
public final class BasicAuthUserDescription extends AbstractUserDescription {

	static final String TYPE = "basic";

	@NotNull private final String name;
	@NotNull private final String email;
	@NotNull private final String password;

	@JsonCreator
	public BasicAuthUserDescription(
			@JsonProperty("name") String name,
			@JsonProperty("email") String email,
			@JsonProperty("role") Role role,
			@JsonProperty("password") String password) {

		super(TYPE, role);
		this.name = name;
		this.email = email;
		this.password = password;
	}


	public String getName() {
		return name;
	}


	public String getEmail() {
		return email;
	}


	public String getPassword() {
		return password;
	}


	@Override
	public boolean equals(Object other) {
		if (!super.equals(other) || !(other instanceof BasicAuthUserDescription)) return false;
		BasicAuthUserDescription user = (BasicAuthUserDescription) other;
		return Objects.equal(name, user.name)
				&& Objects.equal(email, user.email)
				&& Objects.equal(password, user.password);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), name, email, password);
	}

}
