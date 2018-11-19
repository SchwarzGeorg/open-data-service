package org.jvalue.ods.userservice.auth;


import org.jvalue.ods.userservice.user.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RestrictedTo {
	Role value() default Role.ADMIN;
	boolean isOptional() default false;
}
