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
package org.jvalue.ods.data.schema;

import java.util.HashMap;
import java.util.Map;


/**
 * The Class MapValue.
 */
public class MapSchema extends Schema {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The map. */
	private Map<String, Schema> map = new HashMap<String, Schema>();

	
	
	/**
	 * Instantiates a new map schema.
	 *
	 * @param map the map
	 */
	public MapSchema(Map<String, Schema> map) {
		this.map = map;
	}

	/**
	 * Gets the map.
	 * 
	 * @return the map
	 */
	public Map<String, Schema> getMap() {
		return map;
	}

}