package com.demandcube.demandspike.kafka.producer;

import java.util.Properties;
import java.util.Random;

import com.demandcube.demandspike.kafka.metrics.MetricsManager;

import kafka.common.FailedToSendMessageException;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * The Class RandomProducer.
 */
public class RandomProducer implements Runnable {

    /** The message send gap. */
    private int messageSendGap;

    /** The message size. */
    private int messageSize;

    /** The number of messages to send. */
    private int numMessages;

    /** The topic name. */
    private String topicName;

    /** The producer. */
    private Producer<String, String> producer;

    /** The metrics manager. */
    private MetricsManager metricsManager;

    /**
     * Instantiates a new random producer.
     *
     * @param randomProducerConfig
     *            the random producer config
     * @param metricsManager
     *            the metrics manager
     */
    public RandomProducer(RandomProducerConfig randomProducerConfig,
	    MetricsManager metricsManager) {
	this.metricsManager = metricsManager;
	messageSendGap = randomProducerConfig.getMessageSendGap();
	messageSize = randomProducerConfig.getMessageSize();
	numMessages = randomProducerConfig.getNumMessages();
	topicName = randomProducerConfig.getTopicName();
	Properties props = new Properties();
	props.put("metadata.broker.list", randomProducerConfig.getIp() + ":"
		+ randomProducerConfig.getPort());
	props.put("serializer.class", "kafka.serializer.StringEncoder");
	props.put("request.required.acks",
		randomProducerConfig.getRequiredAcks());
	props.put("compression.codec",
		randomProducerConfig.getCompressionCodec());
	props.put("send.buffer.bytes",
		randomProducerConfig.getSendBufferBytes());
	if (!randomProducerConfig.getProducerType().equals("async")) {
	    props.put("producer.type", "async");
	    props.put("batch.num.messages",
		    randomProducerConfig.getBatchNumMessages());
	    props.put("queue.enqueue.timeout.ms",
		    randomProducerConfig.getEnqueueTimeout());
	}
	props.put("client.id", randomProducerConfig.getClientId());
	props.put("request.timeout.ms",
		randomProducerConfig.getRequestTimeout());
	props.put("message.send.max.retries",
		randomProducerConfig.getSendMaxRetries());
	props.put("retry.backoff.ms", randomProducerConfig.getRetryBackoff());
	ProducerConfig config = new ProducerConfig(props);
	producer = new Producer<String, String>(config);

    }

    /**
     * Send random bytes to kafka.
     */
    public void send() {
	KeyedMessage<String, String> data = new KeyedMessage<String, String>(
		topicName, getRandomByteArray(messageSize));
	metricsManager.startSendingNewMessage();
	producer.send(data);
	metricsManager.newMessageSent(messageSize);
    }

    /**
     * Gets the random byte array.
     *
     * @param size
     *            the size
     * @return the random byte array
     */
    public static String getRandomByteArray(int size) {
	byte[] result = new byte[size];
	Random random = new Random();
	random.nextBytes(result);
	return new String(result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
	for (int i = 0; i < numMessages; i++) {
	    try {
		send();
	    } catch (FailedToSendMessageException e) {
		metricsManager.newMessageFailed();
	    }
	    if (messageSendGap != 0) {
		try {
		    Thread.sleep(messageSendGap);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}
	producer.close();
	metricsManager.contextDestroyed();

    }
}
