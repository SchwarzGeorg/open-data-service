package org.jvalue.ods.userservice.communication.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class MessagingConfig {

	@NotNull private final String brokerUrl;
	@NotNull private final String brokerUserName;
	@NotNull private final String brokerPassword;

	@JsonCreator
	public MessagingConfig(
		@JsonProperty("brokerUrl") String brokerUrl,
		@JsonProperty("brokerUserName") String brokerUserName,
		@JsonProperty("brokerPassword") String brokerPassword) {

		this.brokerUrl = brokerUrl;
		this.brokerUserName = brokerUserName;
		this.brokerPassword = brokerPassword;
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public String getBrokerUserName() {
		return brokerUserName;
	}

	public String getBrokerPassword() {
		return brokerPassword;
	}
}
