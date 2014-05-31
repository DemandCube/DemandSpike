package com.demandcube.demandspike.kafka.producer;

import java.util.Properties;
import java.util.Random;

import com.demandcube.demandspike.kafka.metrics.MetricsManager;
import com.demandcube.demandspike.producer.AbstractProducer;
import com.demandcube.demandspike.producer.AbstractProducerConfig;

import kafka.common.FailedToSendMessageException;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * The Class RandomProducer.
 */
public class KafkaRandomProducer extends AbstractProducer {



    /** The topic name. */
    private String topicName;

    /** The producer. */
    private Producer<String, String> producer;


    /**
     * Instantiates a new random producer.
     *
     * @param randomProducerConfig
     *            the random producer config
     * @param metricsManager
     *            the metrics manager
     */
    public KafkaRandomProducer(AbstractProducerConfig producerConfig,
	    MetricsManager metricsManager) {
	super(producerConfig,metricsManager);
	KafkaProducerConfig randomProducerConfig = (KafkaProducerConfig)producerConfig;
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
		topicName, getRandomByteArray());
	producer.send(data);
    }


    @Override
    public void producerDestroyed() {
	producer.close();
    }
}
