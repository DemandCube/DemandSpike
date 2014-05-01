package com.demandcube.demandspike.kafka.producer;

/**
 * The Class TestProducer.
 */
public class Main {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
	    
		RandomProducer rp1 = new RandomProducer("rp1", 1, 255);
		Statistic stat = new Statistic(1);
		stat.monitor(rp1);
		Thread rpT = new Thread(rp1);
		Thread st = new Thread(stat);
		rpT.start();
		st.start();
	}
}
