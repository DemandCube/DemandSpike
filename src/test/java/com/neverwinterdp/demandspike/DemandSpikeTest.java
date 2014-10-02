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
	     String[] args = { "run", "--target", "http://127.0.0.1:7080/message","--protocol", "HTTP", "--method",
	       "POST","--input-data","{\"header\" : {\"version\" : 0.0,\"topic\" : \"metrics.consumer\",\"key\" : \"message-sender-task-0-%myid%\",\"traceEnable\" : false,\"instructionEnable\" : false},\"data\" : {\"type\" : null,\"data\" : \"IkFBQUFBQU=\",\"serializeType\" : null},\"traces\" : null,\"instructions\" : null}","--auto-generator-string","%myid%","--time", "30000", "--cLevel", "10","--maxRequests", "1000" };
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