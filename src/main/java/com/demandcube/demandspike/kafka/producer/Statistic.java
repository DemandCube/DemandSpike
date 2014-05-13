package com.demandcube.demandspike.kafka.producer;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Class Statistic.
 */
public class Statistic implements Runnable {
	
	/** The producers. */
	List<RandomProducer> producers = new ArrayList<RandomProducer>();
	
	/** The period. */
	private int period;

	/** The events sent per period. */
	private long eventsSentPerPeriod;

	/** The bytes sent per period. */
	private long bytesSentPerPeriod;

	/** The events sent total. */
	private long eventsSentTotal;

	/** The bytes sent total. */
	private long bytesSentTotal;

	/** The events sent avg. */
	private double eventsSentAvg;

	/** The second count. */
	private long secondCount;

	/**
	 * Instantiates a new statistic.
	 *
	 * @param period the period
	 */
	public Statistic(int period) {
		this.period = period;
	}

	/**
	 * Monitor.
	 *
	 * @param producer the producer
	 */
	public void monitor(RandomProducer producer) {
		producers.add(producer);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		long previousEventsSentTotal;
		long previousBytesSentTotal;
		while (true) {
			try {
				Thread.sleep(period * 1000);
				secondCount++;
				previousEventsSentTotal = eventsSentTotal;
				previousBytesSentTotal = bytesSentTotal;
				eventsSentTotal = 0;
				bytesSentTotal = 0;
				for (RandomProducer producer : producers) {
					eventsSentTotal += producer.getEventsSent();
					bytesSentTotal += producer.getBytesSent();
				}
				eventsSentPerPeriod = eventsSentTotal - previousEventsSentTotal;
				bytesSentPerPeriod = bytesSentTotal - previousBytesSentTotal;
				eventsSentAvg = (double) eventsSentTotal / secondCount;

				NumberFormat formatter = new DecimalFormat("#0.00");
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				System.out.println(dateFormat.format(date));
				System.out.println("Events Sent Total : " + eventsSentTotal);
				System.out.println("Events Sent per " + period + " second : "
						+ eventsSentPerPeriod);
				System.out.println("Bytes Sent Total : " + bytesSentTotal);
				System.out.println("Bytes Sent per " + period + " second: "
						+ bytesSentPerPeriod);
				System.out.println("Events Sent Average "
						+ formatter.format(eventsSentAvg));
				System.out.println();

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}

}
