package com.demandcube.demandspike.kafka.producer;

import java.util.Map;

import com.demandcube.demandspike.producer.AbstractProducerConfig;

/**
 * The Class RandomProducerConfig.
 */
public class KafkaProducerConfig  extends AbstractProducerConfig{

  
    /** The topic name. */
    String topicName;

    /** The required acks. */
    String requiredAcks;

    /** The compression codec. */
    String compressionCodec;

    /** The send buffer bytes. */
    String sendBufferBytes;

    /** The producer type. */
    String producerType;

    /** The batch num messages. */
    String batchNumMessages;

    /** The enqueue timeout. */
    String enqueueTimeout;

    /** The client id. */
    String clientId;

    /** The request timeout. */
    String requestTimeout;

    /** The send max retries. */
    String sendMaxRetries;

    /** The retry backoff. */
    String retryBackoff;

    /**
     * Instantiates a new random producer config.
     *
     * @param config
     *            the config
     */
    public KafkaProducerConfig(Map config) {
	super(config);
	topicName = config.get("topicName").toString();
	requiredAcks = config.get("requiredAcks").toString();
	compressionCodec = config.get("compressionCodec").toString();
	sendBufferBytes = config.get("sendBufferBytes").toString();
	producerType = config.get("producerType").toString();
	batchNumMessages = config.get("batchNumMessages").toString();
	enqueueTimeout = config.get("enqueueTimeout").toString();
	clientId = config.get("clientId").toString();
	requestTimeout = config.get("requestTimeout").toString();
	sendMaxRetries = config.get("sendMaxRetries").toString();
	retryBackoff = config.get("retryBackoff").toString();
    }

     /**
     * Gets the topic name.
     *
     * @return the topic name
     */
    public String getTopicName() {
	return topicName;
    }

    /**
     * Sets the topic name.
     *
     * @param topicName
     *            the new topic name
     */
    public void setTopicName(String topicName) {
	this.topicName = topicName;
    }

    /**
     * Gets the required acks.
     *
     * @return the required acks
     */
    public String getRequiredAcks() {
	return requiredAcks;
    }

    /**
     * Sets the required acks.
     *
     * @param requiredAcks
     *            the new required acks
     */
    public void setRequiredAcks(String requiredAcks) {
	this.requiredAcks = requiredAcks;
    }

    /**
     * Gets the compression codec.
     *
     * @return the compression codec
     */
    public String getCompressionCodec() {
	return compressionCodec;
    }

    /**
     * Sets the compression codec.
     *
     * @param compressionCodec
     *            the new compression codec
     */
    public void setCompressionCodec(String compressionCodec) {
	this.compressionCodec = compressionCodec;
    }

    /**
     * Gets the send buffer bytes.
     *
     * @return the send buffer bytes
     */
    public String getSendBufferBytes() {
	return sendBufferBytes;
    }

    /**
     * Sets the send buffer bytes.
     *
     * @param sendBufferBytes
     *            the new send buffer bytes
     */
    public void setSendBufferBytes(String sendBufferBytes) {
	this.sendBufferBytes = sendBufferBytes;
    }

    /**
     * Gets the producer type.
     *
     * @return the producer type
     */
    public String getProducerType() {
	return producerType;
    }

    /**
     * Sets the producer type.
     *
     * @param producerType
     *            the new producer type
     */
    public void setProducerType(String producerType) {
	this.producerType = producerType;
    }

    /**
     * Gets the batch num messages.
     *
     * @return the batch num messages
     */
    public String getBatchNumMessages() {
	return batchNumMessages;
    }

    /**
     * Sets the batch num messages.
     *
     * @param batchNumMessages
     *            the new batch num messages
     */
    public void setBatchNumMessages(String batchNumMessages) {
	this.batchNumMessages = batchNumMessages;
    }

    /**
     * Gets the enqueue timeout.
     *
     * @return the enqueue timeout
     */
    public String getEnqueueTimeout() {
	return enqueueTimeout;
    }

    /**
     * Sets the enqueue timeout.
     *
     * @param enqueueTimeout
     *            the new enqueue timeout
     */
    public void setEnqueueTimeout(String enqueueTimeout) {
	this.enqueueTimeout = enqueueTimeout;
    }

    /**
     * Gets the client id.
     *
     * @return the client id
     */
    public String getClientId() {
	return clientId;
    }

    /**
     * Sets the client id.
     *
     * @param clientId
     *            the new client id
     */
    public void setClientId(String clientId) {
	this.clientId = clientId;
    }

    /**
     * Gets the request timeout.
     *
     * @return the request timeout
     */
    public String getRequestTimeout() {
	return requestTimeout;
    }

    /**
     * Sets the request timeout.
     *
     * @param requestTimeout
     *            the new request timeout
     */
    public void setRequestTimeout(String requestTimeout) {
	this.requestTimeout = requestTimeout;
    }

    /**
     * Gets the send max retries.
     *
     * @return the send max retries
     */
    public String getSendMaxRetries() {
	return sendMaxRetries;
    }

    /**
     * Sets the send max retries.
     *
     * @param sendMaxRetries
     *            the new send max retries
     */
    public void setSendMaxRetries(String sendMaxRetries) {
	this.sendMaxRetries = sendMaxRetries;
    }

    /**
     * Gets the retry backoff.
     *
     * @return the retry backoff
     */
    public String getRetryBackoff() {
	return retryBackoff;
    }

    /**
     * Sets the retry backoff.
     *
     * @param retryBackoff
     *            the new retry backoff
     */
    public void setRetryBackoff(String retryBackoff) {
	this.retryBackoff = retryBackoff;
    }
}
