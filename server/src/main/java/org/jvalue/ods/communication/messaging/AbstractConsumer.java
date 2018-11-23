package org.jvalue.ods.communication.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import org.jvalue.commons.utils.Log;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractConsumer {


	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	@NotNull private final String brokerHost;
	@NotNull private final String brokerVHost;
	@NotNull private final String brokerPort;
	@NotNull private final String brokerUsername;
	@NotNull private final String brokerPassword;

	protected AbstractConsumer(
		ConnectionFactory factory,
		MessagingConfig messagingConfig
	) {
		this.factory = factory;
		this.brokerHost = messagingConfig.getBrokerHost();
		this.brokerVHost = messagingConfig.getBrokerVHost();
		this.brokerPort = messagingConfig.getBrokerPort();
		this.brokerUsername = messagingConfig.getBrokerUserName();
		this.brokerPassword = messagingConfig.getBrokerPassword();
	}


	public boolean connect() {
		factory.setHost(brokerHost);
		factory.setVirtualHost(brokerVHost);
		factory.setPort(Integer.parseInt(brokerPort));
		factory.setUsername(brokerUsername);
		factory.setPassword(brokerPassword);

		try {
			Log.info("Connect to RabbitMQ server: " + this.toString());
			connection = factory.newConnection();
			channel = connection.createChannel();
			doSetupBroker(channel);
		} catch (IOException | TimeoutException e) {
			Log.error("Unable to connect to RabbitMQ server: " + this.toString(), e);
			return false;
		}
		return true;
	}

	/**
	 * Setup the broker here.
	 * Especially define exchange and bindings!
	 * @param channel
	 * @throws IOException
	 */
	protected abstract void doSetupBroker(Channel channel) throws IOException;

	protected final void registerConsumer(Consumer consumer) {
		// TODO
	}

	public void close() {
		Log.info("Close connection to RabbitMq server: " + toString());
		try {
			if (this.channel != null && this.channel.isOpen()) {
				this.channel.close();
			}
			if (this.connection != null && this.connection.isOpen()) {
				this.connection.close();
			}
		} catch (IOException | TimeoutException e) {
			Log.error("Unable to disconnect from RabbitMQ server: " + this.toString());
		}
	}


	public abstract String toString();
}
