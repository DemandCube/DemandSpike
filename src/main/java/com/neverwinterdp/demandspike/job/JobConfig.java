package com.neverwinterdp.demandspike.job;

import java.io.Serializable;

import com.neverwinterdp.demandspike.job.send.MessageDriverConfig;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class JobConfig implements Serializable {

	public int numOfTasks = 1;

	public int messageSize = 1024;

	public long maxDuration = 30 * 1000; // 30s

	public long maxNumOfMessage = 1000000; // 1 million messages by default

	public long sendPeriod = 0; // Every 100 ms

	final public MessageDriverConfig driverConfig;

	public JobConfig(MessageDriverConfig driverConfig, int numOfTasks,
			int messageSize, long maxDuration,
			long maxNumOfMessage, long sendPeriod) {
		this.numOfTasks = numOfTasks;
		this.messageSize = messageSize;
		this.maxDuration = maxDuration;
		this.maxNumOfMessage = maxNumOfMessage;
		this.sendPeriod = sendPeriod;
		this.driverConfig = driverConfig;
	}

	public MessageSenderTask createMessageSender(ApplicationMonitor appMonitor,
			String taskId) {
		MessageSenderTask sender = new MessageSenderTask(taskId, appMonitor,
				this);
		return sender;
	}

	public MessageSenderTask[] createMessageSender(ApplicationMonitor appMonitor) {
		MessageSenderTask[] tasks = new MessageSenderTask[numOfTasks];
		for (int i = 0; i < numOfTasks; i++) {
			tasks[i] = new MessageSenderTask("message-sender-task-" + i,appMonitor, this);
		}
		return tasks;
	}
}