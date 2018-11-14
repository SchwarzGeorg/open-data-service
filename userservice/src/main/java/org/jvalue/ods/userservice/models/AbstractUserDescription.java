package org.jvalue.ods.userservice.models;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Objects;

import javax.validation.constraints.NotNull;


@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type",
		visible = true
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = OAuthUserDescription.class, name = OAuthUserDescription.TYPE),
		@JsonSubTypes.Type(value = BasicAuthUserDescription.class, name = BasicAuthUserDescription.TYPE)
})
public abstract class AbstractUserDescription {

	@NotNull private final Role role;
	@NotNull private final String type;

	AbstractUserDescription(String type, Role role) {
		this.type = type;
		this.role = role;
	}


	public String getType() {
		return type;
	}


	public Role getRole() {
		return role;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof AbstractUserDescription)) return false;
		AbstractUserDescription user = (AbstractUserDescription) other;
		return Objects.equal(role, user.role)
				&& Objects.equal(type, user.type);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(role, type);
	}

}
