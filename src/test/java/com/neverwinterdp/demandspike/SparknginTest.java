package com.neverwinterdp.demandspike;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.RouteHandlerGeneric;


public class SparknginTest {
	String data = "{\"header\" : {\"version\" : 0.0,\"topic\" : \"metrics.consumer\",\"key\" : \"message-sender-task-0-%myid%\",\"traceEnable\" : false,\"instructionEnable\" : false},\"data\" : {\"type\" : null,\"data\" : \"IkFBQUFBQU=\",\"serializeType\" : null},\"traces\" : null,\"instructions\" : null}";

	private static HttpServer server;

	@BeforeClass
	public static void setup() throws Exception {
		server = new HttpServer();
		server.add("/message", new MessageHandler());
		server.setPort(7080);
		server.startAsDeamon();
	}

	@AfterClass
	public static void teardown() throws Exception {
		server.shutdown();

	}

	@Test
	public void test30seconds1worker() {
		try {
			System.out.println("Sending messages with cLevel=1 for 30 seconds");
			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					"--auto-generator-string", "%myid%", "--time", "30000" };
			DemandSpike.main(args);
			printSeparator();
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void test30seconds2workers() {
		try {
			System.out.println("Sending messages with cLevel=2 for 30 seconds");
			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					"--auto-generator-string", "%myid%", "--cLevel", "2",
					"--time", "30000" };
			DemandSpike.main(args);
			printSeparator();
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void testSending100000messages() {
		try {
			System.out.println("Sending 100000 messages");

			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					"--auto-generator-string", "%myid%", "--maxRequests",
					"100000" };
			long startTime = System.currentTimeMillis();
			DemandSpike.main(args);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime ;
			System.out.println("100000 message sent in " + elapsedTime+ " ms");
			printSeparator();
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	private void printSeparator() {
		System.out
				.println(" =============================================================================");
	}
	  static public class MessageHandler extends RouteHandlerGeneric {
		    long counter=1;
		    @Override
		    protected void doPost(ChannelHandlerContext ctx, HttpRequest httpReq) {
		      System.out.println("receiving message "+counter);
		      counter++;
		    }
		  }
}
