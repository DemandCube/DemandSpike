package com.neverwinterdp.demandspike.job;

import java.io.Serializable;
import java.nio.channels.ClosedByInterruptException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;

import com.neverwinterdp.demandspike.http.Message;
import com.neverwinterdp.demandspike.job.send.MessageDriver;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class MessageSenderTask implements Runnable, Serializable {
	Logger logger;
	private DemandSpikeJobConfig config;

	String taskId;

	MessageGenerator messageGenerator;
	protected MessageDriver messageDriver;

	public MessageSenderTask(String id, ApplicationMonitor monitor,
			DemandSpikeJobConfig config) {
		this.taskId = id;
		this.config = config;
		messageDriver = config.createDriver(monitor);

		messageGenerator = new MessageGenerator();
		messageGenerator.setIdPrefix(taskId);
		messageGenerator.setMessageSize(config.messageSize);
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void onFinish() {
		messageDriver.close();
	}

	public void run() {
		if (config.sendPeriod > 0)
			sendPeriod();
		else
			sendContinuous();
	}

	void sendContinuous() {
		try {
			long stopTime = System.currentTimeMillis() + config.maxDuration;
			for (long i = 0; i < config.nMessages; i++) {
				Message message = messageGenerator.next();
				messageDriver.send(message);
				if (System.currentTimeMillis() > stopTime)
					break;
			}
		} catch (ClosedByInterruptException ex) {
			logger.warn("Task is closed by the interruption: "
					+ ex.getMessage());
		} catch (Exception ex) {
			logger.error("Task Error", ex);
		} finally {
			onFinish();
		}
	}

	public void sendPeriod() {
		long stopTime = System.currentTimeMillis() + config.maxDuration;
		TimerTask timerTask = new TimerPeriodicTask();
		// running timer task as daemon thread
		Timer timer = new Timer(false);
		timer.scheduleAtFixedRate(timerTask, 0, config.sendPeriod);

		try {
			while (System.currentTimeMillis() < stopTime) {
				Thread.sleep(500);
			}
			timer.cancel();
		} catch (InterruptedException e) {
			timer.cancel();
		} finally {
			onFinish();
		}
	}

	public class TimerPeriodicTask extends TimerTask {
		long messageSent;

		public void run() {
			try {
				Message message = messageGenerator.next();
				messageDriver.send(message);
				messageSent++;
				if (messageSent >= config.nMessages) {
					cancel();
				}
			} catch (Exception e) {
				logger.error("Task Error", e);
				this.cancel();
			}
		}
	}
}