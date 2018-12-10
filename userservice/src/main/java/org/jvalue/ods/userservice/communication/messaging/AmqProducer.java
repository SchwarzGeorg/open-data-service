package org.jvalue.ods.userservice.communication.messaging;

public interface AmqProducer {

	boolean connect();
	void close();

	boolean produce(String routingKey, byte[] message);
}
