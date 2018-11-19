package org.jvalue.ods.userservice.auth.exception;


/**
 * Nope, you didn't have those rights ...
 */
public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException(String message) {
		super(message);
	}


	public UnauthorizedException() { }

}
