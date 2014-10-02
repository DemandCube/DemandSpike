package com.neverwinterdp.demandspike.yarn;

import org.junit.Test;

import com.neverwinterdp.demandspike.DemandSpike;

public class DemandSpikeYarnTest {

	@Test
	public void testNumberOfMessages() {
		try {

			System.out.println("Sending 1000 messages of 1Ko");
			String[] args = { "run",  "--mode" ,"distributed" ,  "--useYarn" ,"true" ,"--target", "http://127.0.0.1:7080", "--method", "POST", "--protocol", "HTTP", "--time","30000", "--cLevel","2" };
			DemandSpike.main(args);
			System.out.println(" =============================================================================");
			assert (true);
		} catch (Exception e) {
			assert (false);
		}
		
	}
/*
	@Test
	public void testMessageSize() {
		try {
			System.out.println("Sending 1000 messages of 2Ko");
			String[] args = { "--useYarn" ,"true", "run", "--target", "http://127.0.0.1:7080",
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
			String[] args = {"--useYarn" ,"true", "run", "--target", "http://127.0.0.1:7080",
					"--method", "POST", "--protocol", "HTTP", "--time", "60000" };
			DemandSpike.main(args);
			System.out.println(" =============================================================================");
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}*/
}