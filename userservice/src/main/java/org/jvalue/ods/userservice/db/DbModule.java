package org.jvalue.ods.userservice.db;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.jvalue.commons.couchdb.CouchDbConfig;
import org.jvalue.commons.couchdb.DbConnectorFactory;
import org.jvalue.ods.userservice.user.UserRepository;

import java.net.MalformedURLException;

public class DbModule extends AbstractModule {

	private final CouchDbConfig couchDbConfig;

	public DbModule(CouchDbConfig couchDbConfig) {
		this.couchDbConfig = couchDbConfig;
	}


	@Override
	protected void configure() {
		try {
			CouchDbInstance couchDbInstance = new StdCouchDbInstance(new StdHttpClient.Builder()
					.url(couchDbConfig.getUrl())
					.username(couchDbConfig.getAdmin().getUsername())
					.password(couchDbConfig.getAdmin().getPassword())
					.maxConnections(couchDbConfig.getMaxConnections())
					.build());
			DbConnectorFactory connectorFactory = new DbConnectorFactory(couchDbInstance, couchDbConfig.getDbPrefix());

			CouchDbConnector userConnector = connectorFactory.createConnector(UserRepository.DATABASE_NAME, true);
			bind(CouchDbConnector.class).annotatedWith(Names.named(UserRepository.DATABASE_NAME)).toInstance(userConnector);

			bind(DbConnectorFactory.class).toInstance(connectorFactory);

		} catch (MalformedURLException mue) {
			throw new RuntimeException(mue);
		}
	}

}
