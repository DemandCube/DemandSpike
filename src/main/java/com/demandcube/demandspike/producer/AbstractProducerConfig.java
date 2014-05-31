package com.demandcube.demandspike.producer;

import java.util.Map;

/**
 * The Class RandomProducerConfig.
 */
public class AbstractProducerConfig {

    /** The message size. */
    int messageSize;

    /** The num messages. */
    int numMessages;

    /** The message send gap. */
    int messageSendGap;

    /** The ip. */
    String ip;

    /** The port. */
    String port;
    
    /**
     * Instantiates a new random producer config.
     *
     * @param config
     *            the config
     */
    public AbstractProducerConfig(Map config) {
	messageSize = Integer.parseInt(config.get("messageSize").toString());
	numMessages = Integer.parseInt(config.get("numMessages").toString());
	messageSendGap = Integer.parseInt(config.get("messageSendGap")
		.toString());
	ip = config.get("ip").toString();
	port = config.get("port").toString();
    }

    /**
     * Gets the message size.
     *
     * @return the message size
     */
    public int getMessageSize() {
	return messageSize;
    }

    /**
     * Sets the message size.
     *
     * @param messageSize
     *            the new message size
     */
    public void setMessageSize(int messageSize) {
	this.messageSize = messageSize;
    }

    /**
     * Gets the num messages.
     *
     * @return the num messages
     */
    public int getNumMessages() {
	return numMessages;
    }

    /**
     * Sets the num messages.
     *
     * @param numMessages
     *            the new num messages
     */
    public void setNumMessages(int numMessages) {
	this.numMessages = numMessages;
    }

    /**
     * Gets the message send gap.
     *
     * @return the message send gap
     */
    public int getMessageSendGap() {
	return messageSendGap;
    }

    /**
     * Sets the message send gap.
     *
     * @param messageSendGap
     *            the new message send gap
     */
    public void setMessageSendGap(int messageSendGap) {
	this.messageSendGap = messageSendGap;
    }

    /**
     * Gets the ip.
     *
     * @return the ip
     */
    public String getIp() {
	return ip;
    }

    /**
     * Sets the ip.
     *
     * @param ip
     *            the new ip
     */
    public void setIp(String ip) {
	this.ip = ip;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public String getPort() {
	return port;
    }

    /**
     * Sets the port.
     *
     * @param port
     *            the new port
     */
    public void setPort(String port) {
	this.port = port;
    }
}
