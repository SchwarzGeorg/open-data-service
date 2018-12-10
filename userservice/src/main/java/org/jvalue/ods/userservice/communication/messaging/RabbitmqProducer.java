package org.jvalue.ods.userservice.communication.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.jvalue.commons.utils.Log;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Producer / Sender for RabbitMQ
 */
public class RabbitmqProducer implements AmqProducer {

	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;

	@NotNull private final String brokerHost;
	@NotNull private final String brokerVHost;
	@NotNull private final String brokerPort;
	@NotNull private final String brokerUsername;
	@NotNull private final String brokerPassword;

	private final String exchangeName;
	private final String exchangeType;

	protected RabbitmqProducer(
		ConnectionFactory factory,
		MessagingConfig messagingConfig,
		String exchangeName,
		String exchangeType
	) {
		this.factory = factory;

		this.brokerHost = messagingConfig.getBrokerHost();
		this.brokerVHost = messagingConfig.getBrokerVHost();
		this.brokerPort = messagingConfig.getBrokerPort();
		this.brokerUsername = messagingConfig.getBrokerUserName();
		this.brokerPassword = messagingConfig.getBrokerPassword();

		this.exchangeName = exchangeName;
		this.exchangeType = exchangeType;
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
			this.setupBroker(channel);
		} catch (IOException | TimeoutException e) {
			Log.error("Unable to connect to RabbitMQ server: " + this.toString(), e);
			return false;
		}
		return true;
	}

	public final boolean produce(String routingKey, byte[] message) {
		try {
			channel.basicPublish(exchangeName, routingKey, null, message);

			Log.debug("[x] Sent '" + message + "' to " + toString());
			return true;
		} catch (NullPointerException | IOException e) {
			Log.debug("[ ] Sent failed '" + message + "' to " + toString());
			return false;
		}
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

	protected void setupBroker(Channel channel) throws IOException {
		channel.exchangeDeclare(exchangeName, exchangeType);
	}

	public String toString() {
		return "{brokerHost: '" + brokerHost +
			"', brokerVHost: '" + brokerVHost +
			"', brokerPort: '" + brokerPort +
			"', exchange_name: '" + exchangeName +
			"', exchange_type: '" + exchangeType + "'}";
	}
}
