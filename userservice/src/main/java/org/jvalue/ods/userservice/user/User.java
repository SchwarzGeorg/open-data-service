package org.jvalue.ods.userservice.user;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import javax.validation.constraints.NotNull;

/**
 * A registered user.
 */
public class User {

	@NotNull private final String id;
	@NotNull private final String name;
	@NotNull private final String email;
	@NotNull private final Role role;

	@JsonCreator
	public User(
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
		if (other == null || !(other instanceof User)) return false;
		User user = (User) other;
		return Objects.equal(id, user.id)
				&& Objects.equal(name, user.name)
				&& Objects.equal(email, user.email)
				&& Objects.equal(role, user.role);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(id, name, email, role);
	}

}
