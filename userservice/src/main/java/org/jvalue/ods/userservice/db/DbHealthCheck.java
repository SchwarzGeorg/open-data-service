package org.jvalue.ods.userservice.db;


import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import org.jvalue.ods.userservice.auth.UserRepository;

/**
 * Checks that CouchDb is reachable.
 */
public final class DbHealthCheck extends HealthCheck {

	private final UserRepository userRepository;

	@Inject
	public DbHealthCheck(UserRepository userRepository) {
		this.userRepository = userRepository;
	}


	@Override
	public Result check() throws Exception {
		userRepository.getAll();
		return Result.healthy();
	}

}
