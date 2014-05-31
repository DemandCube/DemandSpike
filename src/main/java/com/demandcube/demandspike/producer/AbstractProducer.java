package com.demandcube.demandspike.producer;

import java.util.Random;

import kafka.common.FailedToSendMessageException;
import kafka.javaapi.producer.Producer;

import com.demandcube.demandspike.kafka.metrics.MetricsManager;
import com.demandcube.demandspike.kafka.producer.KafkaProducerConfig;

public abstract class AbstractProducer implements Runnable{

    
    
    /** The message send gap. */
    private int messageSendGap;

    /** The message size. */
    private int messageSize;

    /** The number of messages to send. */
    private int numMessages;

    /** The metrics manager. */
    private MetricsManager metricsManager;

    public AbstractProducer(AbstractProducerConfig producerConfig,MetricsManager metricsManager){
	this.metricsManager = metricsManager;
	messageSendGap = producerConfig.getMessageSendGap();
	messageSize = producerConfig.getMessageSize();
	numMessages = producerConfig.getNumMessages();
    }

    /**
     * Send random bytes to kafka.
     * @throws Exception 
     */
    public abstract void send() throws Exception;

    /**
     * Gets the random byte array.
     *
     * @param size
     *            the size
     * @return the random byte array
     */
    public  String getRandomByteArray() {
	byte[] result = new byte[messageSize];
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
		metricsManager.startSendingNewMessage();
		send();
		metricsManager.newMessageSent(messageSize);
	    } catch (FailedToSendMessageException e) {
		metricsManager.newMessageFailed();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
	    }
	    if (messageSendGap != 0) {
		try {
		    Thread.sleep(messageSendGap);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}
	metricsManager.contextDestroyed();
	producerDestroyed();

    }
    public abstract void producerDestroyed(); 
}
