package com.demandcube.demandspike.kafka.producer;

import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * The Class RandomProducer.
 */
public class RandomProducer implements Runnable {
    

	/** The events sent. */
	private int eventsSent;

	/** The bytes sent. */
	private int bytesSent;

	/** The period. */
	private int period;

	/** The name. */
	private String name;

	/** The max size. */
	private int maxSize;
	private String ip;
	private String port;
	private String topicName;

	/**
	 * Instantiates a new random producer.
	 *
	 * @param name the name
	 * @param period the period
	 * @param maxSize the max size
	 */
	public RandomProducer(String name, int period, int maxSize,String ip, String port,String topicName) {
		this.name = name;
		this.period = period;
		this.maxSize = maxSize;
		this.ip  = ip;
		this.port = port;
		this.topicName = topicName;
	}

	/**
	 * Gets the events sent.
	 *
	 * @return the events sent
	 */
	public int getEventsSent() {
		return eventsSent;
	}

	/**
	 * Gets the bytes sent.
	 *
	 * @return the bytes sent
	 */
	public int getBytesSent() {
		return bytesSent;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Send random bytes to kafka
	 */
	public void send() {
		Properties props = new Properties();
		props.put("metadata.broker.list", ip+":"+port);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);
		Producer<String, String> producer = new Producer<String, String>(config);
		Random random = new Random();
		int byteSize = random.nextInt(maxSize);
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(
			topicName, getRandomByteArray(byteSize));
		producer.send(data);
		producer.close();
		eventsSent++;
		bytesSent += byteSize;
	}

	/**
	 * Gets the random byte array.
	 *
	 * @param size the size
	 * @return the random byte array
	 */
	public static String getRandomByteArray(int size) {
		byte[] result = new byte[size];
		Random random = new Random();
		random.nextBytes(result);
		return new String(result);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			send();
			try {
				Thread.sleep(period * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
