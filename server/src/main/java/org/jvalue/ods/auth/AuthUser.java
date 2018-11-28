package org.jvalue.ods.auth;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A registered user.
 */
public class AuthUser implements Serializable {

	@NotNull private final String id;
	@NotNull private final String name;
	@NotNull private final String email;
	@NotNull private final Role role;

	@JsonCreator
	public AuthUser(
			@JsonProperty("id") String id,
			@JsonProperty("name") String name,
			@JsonProperty("email") String email,
			@JsonProperty("role") Role role) {

		this.id = id;
		this.email = email;
		this.name = name;
		this.role = role;
	}


	public String getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public String getEmail() {
		return email;
	}


	public Role getRole() {
		return role;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof AuthUser)) return false;
		AuthUser authUser = (AuthUser) other;
		return Objects.equal(id, authUser.id)
				&& Objects.equal(name, authUser.name)
				&& Objects.equal(email, authUser.email)
				&& Objects.equal(role, authUser.role);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(id, name, email, role);
	}

}
