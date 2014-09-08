package com.neverwinterdp.demandspike.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJob implements Runnable {
	protected static final Logger LOGGER = LoggerFactory
			.getLogger(DemandSpikeJob.class);

	ApplicationMonitor appMonitor;
	JobConfig config;
	String name;

	public DemandSpikeJob(String name, ApplicationMonitor monitor, JobConfig config) {
		this.name = name;
		this.appMonitor = monitor;
		this.config = config;

	}

	public String getName() {
		return name;
	}

	public void setName(String id) {
		this.name = id;
	}

	public void run() {
		ExecutorService taskExecutor = null;
		try {
			taskExecutor = Executors.newFixedThreadPool(config.numOfTasks);
			MessageSenderTask[] task = config.createMessageSender(appMonitor);
			for (int i = 0; i < task.length; i++) {
				task[i].setLogger(LOGGER);
				taskExecutor.submit(task[i]);
			}
			taskExecutor.shutdown();
			taskExecutor.awaitTermination(config.maxDuration,
					TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		} catch (Exception ex) {
			LOGGER.error("Unexpected error", ex);
		} finally {
			if (taskExecutor != null) {
				taskExecutor.shutdownNow();
			}
		}
	}
}
