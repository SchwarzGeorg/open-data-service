package org.jvalue.ods.communication.messaging;

import com.rabbitmq.client.*;
import org.jvalue.ods.utils.JsonMapper;

import java.io.IOException;

public class UserEventConsumer extends AbstractConsumer {

	private static final String EXCHANGE_NAME = "user-event-exchange";
	private static final String EXCHANGE_TYPE = "topic";
	private static final String ROUTING_KEY = "user.#"; // user.anything
	private MessagingConfig messagingConfig;
	private String queueName;


	public UserEventConsumer(ConnectionFactory factory, MessagingConfig messagingConfig) {
		super(factory, messagingConfig);
		this.messagingConfig = messagingConfig;
	}

	@Override
	protected void doSetupBroker(Channel channel) throws IOException {
		channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
		this.queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
	}

	@Override
	protected String getQueueName() {
		return queueName;
	}

	public void registerEventHandler(UserEventHandler eventHandler) {
		Consumer consumer = new DefaultConsumer(getChannel()) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
				UserEvent userEvent = JsonMapper.readValue(message, UserEvent.class);

				eventHandler.handleEvent(userEvent);
			}
		};

		registerConsumer(getQueueName(), consumer);
	}

	@Override
	public String toString() {
		return "{brokerHost: '" + messagingConfig.getBrokerHost() +
			"', brokerVHost: '" + messagingConfig.getBrokerVHost() +
			"', brokerPort: '" + messagingConfig.getBrokerPort() +
			"', exchange_name: '" + EXCHANGE_NAME +
			"', exchange_type: '" + EXCHANGE_TYPE + "'}";
	}
}
