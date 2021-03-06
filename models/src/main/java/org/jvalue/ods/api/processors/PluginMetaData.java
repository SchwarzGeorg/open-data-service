/*
 * Copyright (c) 2019 Friedrich-Alexander University Erlangen-Nuernberg (FAU)
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.jvalue.ods.api.processors;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class PluginMetaData {

	@NotNull private final String id, author;

	@JsonCreator
	public PluginMetaData(
			@JsonProperty("id") String id,
			@JsonProperty("author") String author) {

		this.id = id;
		this.author = author;
	}


	@Schema(hidden = true)
	public String getId() {
		return id;
	}


	@Schema(example = "Walter Frosch", required = true)
	public String getAuthor() {
		return author;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof PluginMetaData)) return false;
		if (other == this) return true;
		PluginMetaData metaData = (PluginMetaData) other;
		return Objects.equal(id, metaData.id)
				&& Objects.equal(author, metaData.author);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(id, author);
	}

}
