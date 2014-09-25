package com.neverwinterdp.demandspike;

import org.junit.Test;

public class DemandSpikeTest {

	@Test
	public void testNumberOfMessages() {
		try {

			System.out.println("Sending 1000 messages of 1Ko");
			String[] args = { "run", "--target", "http://127.0.0.1:7080",
					"--method", "POST", "--protocol", "HTTP" };
			DemandSpike.main(args);
			System.out.println(" =============================================================================");

		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void testMessageSize() {
		try {
			System.out.println("Sending 1000 messages of 2Ko");
			String[] args = { "run", "--target", "http://127.0.0.1:7080",
					"--method", "POST", "--protocol", "HTTP", "--messageSize",
					"2048" };
			DemandSpike.main(args);
			System.out.println(" =============================================================================");
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void testSendingPeriod() {
		try {
			System.out.println("Sending messages for 1 munite");
			String[] args = { "run", "--target", "http://127.0.0.1:7080",
					"--method", "POST", "--protocol", "HTTP", "--time", "60000" };
			DemandSpike.main(args);
			System.out.println(" =============================================================================");
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}
}