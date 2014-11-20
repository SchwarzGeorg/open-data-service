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
package org.jvalue.ods.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.jvalue.ods.data.DataSource;
import org.jvalue.ods.db.SourceDataRepository;
import org.jvalue.ods.utils.Assert;

import java.util.List;


final class DbInsertionFilter implements Filter<Object, Object> {

	private final SourceDataRepository dataRepository;
	private final DataSource source;

	@Inject
	DbInsertionFilter(
			@Assisted SourceDataRepository dataRepository,
			@Assisted DataSource source) {

		Assert.assertNotNull(source);
		this.dataRepository = dataRepository;
		this.source = source;
	}


	@Override
	@SuppressWarnings("unchecked")
	public Object filter(Object data) {
		if (!(data instanceof List)) throw new IllegalArgumentException("can only work with lists");

		List<JsonNode> list = (List<JsonNode>) data;
		for (JsonNode node : list) dataRepository.add(node);

		return data;
	}

}