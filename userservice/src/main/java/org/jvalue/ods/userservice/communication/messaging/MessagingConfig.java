package org.jvalue.ods.userservice.communication.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class MessagingConfig {

	@NotNull private final String brokerHost;
	@NotNull private final String brokerVHost;
	@NotNull private final String brokerPort;
	@NotNull private final String brokerManagementPort;
	@NotNull private final String brokerUserName;
	@NotNull private final String brokerPassword;

	@JsonCreator
	public MessagingConfig(
		@JsonProperty("brokerHost") String brokerHost,
		@JsonProperty("brokerVHost") String brokerVHost,
		@JsonProperty("brokerPort") String brokerPort,
		@JsonProperty("brokerManagementPort") String brokerManagementPort,
		@JsonProperty("brokerUserName") String brokerUserName,
		@JsonProperty("brokerPassword") String brokerPassword) {

		this.brokerHost = brokerHost;
		this.brokerVHost = brokerVHost;
		this.brokerPort = brokerPort;
		this.brokerManagementPort = brokerManagementPort;
		this.brokerUserName = brokerUserName;
		this.brokerPassword = brokerPassword;
	}

	public String getBrokerHost() {
		return brokerHost;
	}

	public String getBrokerVHost() {
		return brokerVHost;
	}

	public String getBrokerPort() {
		return brokerPort;
	}

	public String getBrokerManagementPort() {
		return brokerManagementPort;
	}

	public String getBrokerUserName() {
		return brokerUserName;
	}

	public String getBrokerPassword() {
		return brokerPassword;
	}
}
