package com.neverwinterdp.demandspike.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.demandspike.job.send.HttpDriver;
import com.neverwinterdp.demandspike.job.send.MessageDriver;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJobConfig {
	private String brokerConnect;

	private String topic;

	public int cLevel = 1;

	public int messageSize = 1024;

	public long maxDuration = 30 * 1000; // 30s

	public long nMessages = 1000000; // 1 million messages by default

	public long sendPeriod = 0; // Every 100 ms

	String driver = "sparkngin";

	Map<String, String> driverProperties = new HashMap<String, String>();

	List<String> connect = new ArrayList<String>();
	
	Method method;

	public String getDriver() {
		return this.driver;
	}

	public MessageDriver createDriver(ApplicationMonitor appMonitor) {
		MessageDriver mdriver = new HttpDriver(appMonitor);
		mdriver.init(connect, this.method, this.topic);
		return mdriver;
	}

	public DemandSpikeJobConfig(List<String> connect, Method method, int numOfTasks, int messageSize, long maxDuration,
			long maxNumOfMessage, long sendPeriod) {
		this.cLevel = numOfTasks;
		this.messageSize = messageSize;
		this.maxDuration = maxDuration;
		this.nMessages = maxNumOfMessage;
		this.sendPeriod = sendPeriod;
		this.connect = connect;
		// To Do parse topic and driver
		//this.topic = topic;
		//$+this.driver = driver;

	}

	public DemandSpikeJobConfig(Map<String, String> conf) {
		this.brokerConnect = getString(conf, "broker-connect", "127.0.0.1:8080");
		this.topic = getString(conf, "topic", "metrics.consumer");
		this.nMessages = getInt(conf, "num-of-message", 1000000);
		this.maxDuration = getLong(conf, "max-duration", (3 * 60 * 1000l));
		this.messageSize = getInt(conf, "message-size", 1024);

	}

	public int getMessageSize() {
		return messageSize;
	}

	public void setMessageSize(int messageSize) {
		this.messageSize = messageSize;
	}

	public String getBrokerConnect() {
		return brokerConnect;
	}

	public String getTopic() {
		return topic;
	}

	public long getMaxDuration() {
		return maxDuration;
	}

	public long getNumOfMessages() {
		return nMessages;
	}

	private String getString(Map<String, String> map, String name,
			String defaultValue) {
		String val = map.get(name);
		if (val == null)
			return defaultValue;
		return val;
	}

	private int getInt(Map<String, String> map, String name, int defaultValue) {
		String val = map.get(name);
		if (val == null)
			return defaultValue;
		return Integer.parseInt(val);
	}

	private long getLong(Map<String, String> map, String name, long defaultValue) {
		String val = map.get(name);
		if (val == null)
			return defaultValue;
		return Long.parseLong(val);
	}

	public MessageSenderTask createMessageSender(ApplicationMonitor appMonitor,
			String taskId) {
		MessageSenderTask sender = new MessageSenderTask(taskId, appMonitor,
				this);
		return sender;
	}

	public MessageSenderTask[] createMessageSender(ApplicationMonitor appMonitor) {
		MessageSenderTask[] tasks = new MessageSenderTask[cLevel];
		for (int i = 0; i < cLevel; i++) {
			tasks[i] = new MessageSenderTask("message-sender-task-" + i,
					appMonitor, this);
		}
		return tasks;
	}
	
}