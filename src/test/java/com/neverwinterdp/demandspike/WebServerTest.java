package com.neverwinterdp.demandspike;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.HttpConnectionUnitTest.LongTaskRouteHandler;

public class WebServerTest {
	/*
	 * private static HttpServer server ;
	 * 
	 * @BeforeClass public static void setup() throws Exception { server = new
	 * HttpServer(); server.add("/message", new LongTaskRouteHandler());
	 * server.setPort(7080); server.startAsDeamon() ; }
	 * 
	 * @AfterClass public static void teardown() throws Exception {
	 * server.shutdown();
	 * 
	 * }
	 */

	@Test
	public void testMessageSize() {
		try {
			System.out.println("Sending 1000 messages of 2Ko");
			String[] args = {
					"run",
					"--target",
					"http://127.0.0.1:7080/message",
					"--protocol",
					"HTTP",
					"--method",
					"POST",
					"--input-data",
					"{\"header\" : {\"version\" : 0.0,\"topic\" : \"metrics.consumer\",\"key\" : \"message-sender-task-0-%myid%\",\"traceEnable\" : false,\"instructionEnable\" : false},\"data\" : {\"type\" : null,\"data\" : \"IkFBQUFBQU=\",\"serializeType\" : null},\"traces\" : null,\"instructions\" : null}",
					"--auto-generator-string", "%myid%", "--time", "300000",
					"--cLevel", "1", "--maxRequests", "1000" };
			DemandSpike.main(args);
			System.out
					.println(" =============================================================================");
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

}
