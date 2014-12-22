package org.jvalue.ods.db;


import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.DocumentOperationResult;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.DesignDocument;
import org.ektorp.support.DesignDocumentFactory;
import org.ektorp.support.StdDesignDocumentFactory;
import org.jvalue.ods.data.DataView;
import org.jvalue.ods.utils.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataRepository extends CouchDbRepositorySupport<JsonNode> {

	private static final String DESIGN_DOCUMENT_ID = "_design/" + JsonNode.class.getSimpleName();
	private static final DesignDocumentFactory designFactory = new StdDesignDocumentFactory();

	private final CouchDbConnector connector;
	private final DataView domainIdView;


	@Inject
	DataRepository(CouchDbInstance couchDbInstance, @Assisted String databaseName, @Assisted JsonPointer domainIdKey) {
		super(JsonNode.class, couchDbInstance.createConnector(databaseName, true));
		this.connector = couchDbInstance.createConnector(databaseName, true);
		initStandardDesignDocument();

		domainIdView = createDomainIdView(domainIdKey);
		if (!containsView(domainIdView)) addView(domainIdView);

		DataView allView = createAllView(domainIdKey);
		if (!containsView(allView)) addView(allView);
	}


	public JsonNode findByDomainId(String domainId) {
		List<JsonNode> resultList = executeQuery(domainIdView, domainId);
		if (resultList.isEmpty()) throw new DocumentNotFoundException(domainId);
		else if (resultList.size() == 1) return resultList.get(0);
		else throw new IllegalStateException("found more than one element for given domain id");
	}


	public List<JsonNode> executeQuery(DataView view, String param) {
		if (param == null) return queryView(view.getViewId());
		else return queryView(view.getViewId(), param);
	}


	public void addView(DataView dataView) {
		Assert.assertNotNull(dataView);

		DesignDocument designDocument;
		boolean update = false;

		if (connector.contains(DESIGN_DOCUMENT_ID)) {
			designDocument = connector.get(DesignDocument.class, DESIGN_DOCUMENT_ID);
			update = true;
		} else {
			designDocument = designFactory.newDesignDocumentInstance();
			designDocument.setId(DESIGN_DOCUMENT_ID);
		}

		DesignDocument.View view;
		if (dataView.getReduceFunction() == null) view = new DesignDocument.View(dataView.getMapFunction());
		else view = new DesignDocument.View(dataView.getMapFunction(), dataView.getReduceFunction());
		designDocument.addView(dataView.getViewId(), view);

		if (update) connector.update(designDocument);
		else connector.create(designDocument);
	}


	public void removeView(DataView view) {
		Assert.assertNotNull(view);

		if (!connector.contains(DESIGN_DOCUMENT_ID)) return;
		DesignDocument designDocument = connector.get(DesignDocument.class, DESIGN_DOCUMENT_ID);
		designDocument.removeView(view.getViewId());
		connector.update(designDocument);
	}


	public boolean containsView(DataView dataView) {
		Assert.assertNotNull(dataView);

		if (!connector.contains(DESIGN_DOCUMENT_ID)) return false;
		DesignDocument designDocument = connector.get(DesignDocument.class, DESIGN_DOCUMENT_ID);
		return designDocument.containsView(dataView.getViewId());
	}


	public Map<String, JsonNode> executeBulkGet(Collection<String> ids) {
		ViewQuery query = new ViewQuery()
				.designDocId(DESIGN_DOCUMENT_ID)
				.viewName(domainIdView.getViewId())
				.includeDocs(true)
				.keys(ids);

		Map<String, JsonNode> nodes = new HashMap<>();
		for (ViewResult.Row row : connector.queryView(query).getRows()) {
			nodes.put(row.getKey(), row.getDocAsNode());
		}
		return nodes;
	}


	public Collection<DocumentOperationResult> executeBulkCreateAndUpdate(Collection<JsonNode> data) {
		return connector.executeBulk(data);
	}


	private DataView createDomainIdView(JsonPointer domainIdKey) {
		String domainIdViewName = "findByDomainId";
		String domainIdProperty = createDomainIdJavascriptProperty(domainIdKey);

		StringBuilder mapBuilder = new StringBuilder();
		mapBuilder.append("function(doc) { if (");
		mapBuilder.append(domainIdProperty);
		mapBuilder.append(") emit(");
		mapBuilder.append(domainIdProperty);
		mapBuilder.append(", doc) }");
		return new DataView(domainIdViewName, mapBuilder.toString());
	}


	private DataView createAllView(JsonPointer domainIdKey) {
		String domainIdViewName = "all";
		String domainIdProperty = createDomainIdJavascriptProperty(domainIdKey);

		StringBuilder mapBuilder = new StringBuilder();
		mapBuilder.append("function(doc) { if (");
		mapBuilder.append(domainIdProperty);
		mapBuilder.append(") emit(null,doc) }");
		return new DataView(domainIdViewName, mapBuilder.toString());
	}


	private String createDomainIdJavascriptProperty(JsonPointer domainIdKey) {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append("doc");
		JsonPointer pointer = domainIdKey;
		while (pointer != null && !pointer.toString().isEmpty()) {
			if (pointer.mayMatchProperty()) {
				keyBuilder.append(".");
				keyBuilder.append(pointer.getMatchingProperty());
			} else {
				keyBuilder.append("[");
				keyBuilder.append(pointer.getMatchingIndex());
				keyBuilder.append("]");
			}
			pointer = pointer.tail();
		}
		return keyBuilder.toString();
	}

}
