package org.jvalue.ods.userservice.auth.credentials;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.jvalue.commons.couchdb.DbDocument;
import org.jvalue.commons.couchdb.DbDocumentAdaptable;
import org.jvalue.commons.couchdb.RepositoryAdapter;
import org.jvalue.ods.userservice.user.UserRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public final class BasicCredentialsRepository extends RepositoryAdapter<
		BasicCredentialsRepository.CredentialsCouchDbRepository,
		BasicCredentialsRepository.CredentialsDocument,
	BasicCredentials> {

	private static final String DOCUMENT_ID = "doc.value.userId != null && doc.value.salt != null";

	@Inject
	BasicCredentialsRepository(@Named(UserRepository.DATABASE_NAME) CouchDbConnector connector) {
		super(new CredentialsCouchDbRepository(connector));
	}


	@View( name = "all", map = "function(doc) { if (" + DOCUMENT_ID + ") emit(null, doc)}")
	static final class CredentialsCouchDbRepository
			extends CouchDbRepositorySupport<CredentialsDocument>
			implements DbDocumentAdaptable<CredentialsDocument, BasicCredentials> {


		public CredentialsCouchDbRepository(CouchDbConnector connector) {
			super(CredentialsDocument.class, connector);
			initStandardDesignDocument();
		}


		@Override
		@View(name = "by_id", map = "function(doc) { if (" + DOCUMENT_ID + ") emit(doc.value.userId, doc._id)}")
		public CredentialsDocument findById(String userId) {
			List<CredentialsDocument> credentials = queryView("by_id", userId);
			if (credentials.isEmpty()) throw new DocumentNotFoundException(userId);
			if (credentials.size() > 1)
				throw new IllegalStateException("found more than one credentials for id " + userId);
			return credentials.get(0);
		}


		@Override
		public CredentialsDocument createDbDocument(BasicCredentials credentials) {
			return new CredentialsDocument(credentials);
		}


		@Override
		public String getIdForValue(BasicCredentials credentials) {
			return credentials.getUserId();
		}
	}


	static final class CredentialsDocument extends DbDocument<BasicCredentials> {

		@JsonCreator
		public CredentialsDocument(@JsonProperty("value") BasicCredentials credentials) {
			super(credentials);
		}

	}

}
