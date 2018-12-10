package org.jvalue.ods.communication.messaging.consumer;

import com.rabbitmq.client.*;
import org.jvalue.commons.utils.Log;
import org.jvalue.ods.communication.messaging.MessagingConfig;
import org.jvalue.ods.communication.messaging.event.EventHandler;
import org.jvalue.ods.communication.messaging.event.MessagingEvent;
import org.jvalue.ods.utils.JsonMapper;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitmqConsumer<T extends MessagingEvent> implements AmqpConsumer<T> {


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
	private final String routingKey;
	private String queueName;

	public RabbitmqConsumer(
		ConnectionFactory factory,
		MessagingConfig messagingConfig,
		String exchangeName,
		String exchangeType,
		String routingKey
	) {
		this.factory = factory;

		this.brokerHost = messagingConfig.getBrokerHost();
		this.brokerVHost = messagingConfig.getBrokerVHost();
		this.brokerPort = messagingConfig.getBrokerPort();
		this.brokerUsername = messagingConfig.getBrokerUserName();
		this.brokerPassword = messagingConfig.getBrokerPassword();

		this.exchangeName = exchangeName;
		this.exchangeType = exchangeType;
		this.routingKey = routingKey;
	}

	@Override
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
			setupBroker(channel);
		} catch (IOException | TimeoutException e) {
			Log.error("Unable to connect to RabbitMQ server: " + this.toString(), e);
			return false;
		}
		return true;
	}

	private void registerConsumer(String queueName, Consumer consumer) {
		try {
			channel.basicConsume(queueName, consumer);
		} catch (IOException e) {
			Log.error("Error receiving message from RabbitMQ", e);
		}
	}

	@Override
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

	private void setupBroker(Channel channel) throws IOException {
		channel.exchangeDeclare(exchangeName, exchangeType);
		this.queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, exchangeName, routingKey);
	}

	@Override
	public void registerEventHandler(EventHandler<T> eventHandler) {
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
				T event = JsonMapper.readValue(message, eventHandler.getHandledEventClass());

				eventHandler.handleEvent(event);
			}
		};

		registerConsumer(queueName, consumer);
	}

	@Override
	public String toString() {
		return "{brokerHost: '" + brokerHost +
			"', brokerVHost: '" + brokerVHost+
			"', brokerPort: '" + brokerPort +
			"', exchange_name: '" + exchangeName +
			"', exchange_type: '" + exchangeType + "'}";
	}
}
