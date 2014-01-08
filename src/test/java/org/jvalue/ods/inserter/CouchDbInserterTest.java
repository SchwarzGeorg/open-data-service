/*  Open Data Service
    Copyright (C) 2013  Tsysin Konstantin, Reischl Patrick

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
 */
package org.jvalue.ods.inserter;

import static org.junit.Assert.*;

import org.ektorp.support.CouchDbDocument;
import org.ektorp.util.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvalue.ods.db.CouchDbInserter;

/**
 * The Class CouchDbInserterTest.
 */
public class CouchDbInserterTest {

	/** The test db name. */
	private final String testDbName = "CouchDbInserterTest";

	/** The data. */
	private final CouchDbDocument data = new CouchDbDocument();

	/** The couch db inserter. */
	private CouchDbInserter couchDbInserter;

	/**
	 * Initialize.
	 */
	@Before
	public void initialize() {
		// This test needs installed Apache CouchDB!
		couchDbInserter = new CouchDbInserter(testDbName);
		assertNotNull(couchDbInserter);
	}

	// Constructor Tests
	/**
	 * Test couch db inserter constructor empty database name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCouchDbInserterConstructorEmptyDatabaseName() {
		String databaseName = "";
		CouchDbInserter couchDbInserter = new CouchDbInserter(databaseName);
		Assert.notNull(couchDbInserter);
	}

	/**
	 * Test couch db inserter constructor null database name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCouchDbInserterConstructorNullDatabaseName() {
		String databaseName = null;
		CouchDbInserter couchDbInserter = new CouchDbInserter(databaseName);
		Assert.notNull(couchDbInserter);
	}

	// Method Tests
	/**
	 * Test insert method.
	 */
	@Test
	public void testInsert() {
		couchDbInserter.insert(data);
	}

	//TODO: null data
	
}