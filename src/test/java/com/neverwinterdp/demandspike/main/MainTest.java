package com.neverwinterdp.demandspike.main;

import org.junit.Test;

import com.neverwinterdp.demandspike.DemandSpike;

public class MainTest {

	 @Test
	 	 public void testMessageSize() {
	 	   try {
	 	     System.out.println("Sending 1000 messages of 2Ko");
	 	     String[] args = { "run", "--target", "http://127.0.0.1:7080/message","--protocol", "HTTP", "--method",
	 	       "POST","--input-data","{\"header\" : {\"version\" : 0.0,\"topic\" : \"metrics.consumer\",\"key\" : \"message-sender-task-0-%myid%\",\"traceEnable\" : false,\"instructionEnable\" : false},\"data\" : {\"type\" : null,\"data\" : \"IkFBQUFBQU=\",\"serializeType\" : null},\"traces\" : null,\"instructions\" : null}","--auto-generator-string","%myid%","--time", "300000", "--cLevel", "2","--maxRequests", "100000" };
	 	     DemandSpike.main(args);
	 	     System.out.println(" =============================================================================");
	 	   } catch (Exception e) {
	 	     assert (false);
	 	   }
	 	   assert (true);
	 	 }

}
