/*
 * 18.04.2013
 */
package com.demandcube.demandspike.kafka.metrics;

import com.yammer.metrics.JmxReporter;
import com.yammer.metrics.Meter;
import com.yammer.metrics.MetricRegistry;
import com.yammer.metrics.Timer;

/**
 * The Class MetricsManager.
 */
public class MetricsManager  {

    /** The metrics. */
    private static MetricRegistry metrics;
    
    /** The jmx. */
    private JmxReporter jmx;
    
    /** The bytes per second. */
    private  Meter bytesPerSecond;
    
    /** The message per second. */
    private  Meter messagePerSecond;
    
    /** The failure per second. */
    private  Meter failurePerSecond;
    
    /** The latency. */
    private   Timer latency;
    
    /** The context. */
    private Timer.Context context;

    /**
     * Instantiates a new metrics manager.
     *
     * @param producerName the producer name
     * @param topicName the topic name
     */
    public MetricsManager(String producerName, String topicName){
	String prefix = producerName+"-"+topicName+"-";
	metrics = new MetricRegistry("demandspike.kafka.producer");
	bytesPerSecond = metrics.meter(prefix+"BytesPerSecond");
	messagePerSecond = metrics.meter(prefix+"MessagesPerSecond");
        failurePerSecond = metrics.meter(prefix+"FailuresPerSecond");
        latency = metrics.timer(prefix+"Latency");
	jmx = JmxReporter.forRegistry(metrics).build();
	jmx.start();
    }

    /**
     * Start sending new message.
     */
    public void startSendingNewMessage() {
	context = latency.time();
    }

    /**
     * New message sent.
     *
     * @param bytes the bytes
     */
    public void newMessageSent(long bytes) {
	context.stop();
	bytesPerSecond.mark(bytes);
	messagePerSecond.mark();	
    }
    
    /**
     * New message failed.
     */
    public void newMessageFailed() {
 	failurePerSecond.mark();
     }

    /**
     * Context destroyed.
     */
    public void contextDestroyed() {
	jmx.stop();
    }




}
