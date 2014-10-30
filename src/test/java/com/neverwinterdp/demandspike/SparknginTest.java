package com.neverwinterdp.demandspike;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.RouteHandlerGeneric;

public class SparknginTest {
	String data = "{\"header\" : {\"version\" : 0.0,\"topic\" : \"metrics.consumer\",\"key\" : \"message-sender-task-0\",\"traceEnable\" : false,\"instructionEnable\" : false},\"data\" : {\"type\" : null,\"data\" : \"IkFBQUFBQU=\",\"serializeType\" : null},\"traces\" : null,\"instructions\" : null}";
	private static HttpServer server;
	private static String failureCondition="";

	
	
	
	public static void main(String[] args)  {
		server = new HttpServer();
		server.add("/message", new MessageHandler());
		server.setPort(7080);
		server.startAsDeamon();
	}
	
	@BeforeClass
	public static void setup()  {
		server = new HttpServer();
		server.add("/message", new MessageHandler());
		server.setPort(7080);
	}

	@AfterClass
	public static void teardown() throws Exception {
		

	}

	@Test
	public void testThirtysecondsOneWorker() {
		try {
			server.startAsDeamon();
			System.out.println("Sending messages with cLevel=1 for 30 seconds");
			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					 "--time", "30000" };
			DemandSpike.main(args);
			server.shutdown();
			printSeparator();
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void testThirtySecondsTwoWorkers() {
		try {
			server.startAsDeamon();
			System.out.println("Sending messages with cLevel=2 for 30 seconds");
			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					 "--cLevel", "2",
					"--time", "30000" };
			DemandSpike.main(args);
			server.shutdown();
			printSeparator();
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void testSendingMillionMessages() {
		try {
			server.startAsDeamon();
			System.out.println("Sending million messages");

			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					"--maxRequests","10000"};
			long startTime = System.currentTimeMillis();
			DemandSpike.main(args);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("Execution time " + elapsedTime + " ms");
			server.shutdown();
			printSeparator();
			
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void testWithAllFailues() {
		try {
			server.startAsDeamon();
			failureCondition = "All";
			System.out.println("test With All Failues");

			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					 "--maxRequests",
					"100000", "--rate", "1000", "--stopOnFailure", "20",
					"--stopOnCondition", "All" };
			long startTime = System.currentTimeMillis();
			DemandSpike.main(args);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("Execution time " + elapsedTime + " ms");
			server.shutdown();
			printSeparator();
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void testWithLatencyFailures() {
		try {
			server.startAsDeamon();
			failureCondition = "Latency";
			System.out.println("test With Latency Failures");

			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					 "--maxRequests",
					"100000", "--rate", "1000", "--stopOnFailure", "20",
					"--stopOnCondition", "Latency:1500" };
			long startTime = System.currentTimeMillis();
			DemandSpike.main(args);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("Execution time " + elapsedTime + " ms");
			server.shutdown();
			printSeparator();
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void testWithColoedConnexionFailures() {
		try {
			server.startAsDeamon();
			failureCondition = "FailedConnexion";
			System.out.println("test With Coloed Connexion Failures");

			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					 "--maxRequests",
					"100000", "--rate", "1000", "--stopOnFailure", "20",
					"--stopOnCondition", "FailedConnexion" };
			long startTime = System.currentTimeMillis();
			DemandSpike.main(args);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("Execution time " + elapsedTime + " ms");
			server.shutdown();
			printSeparator();
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	@Test
	public void testWithIOFailures() {
		try {
			server.startAsDeamon();
			failureCondition = "FailedIO";
			System.out.println("test With IO Failures");

			String[] args = { "run", "--target",
					"http://127.0.0.1:7080/message", "--protocol", "HTTP",
					"--method", "POST", "--input-data", this.data,
					 "--maxRequests",
					"100000", "--rate", "1000", "--stopOnFailure", "20",
					"--stopOnCondition", "FailedIO" };
			long startTime = System.currentTimeMillis();
			DemandSpike.main(args);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("Execution time " + elapsedTime + " ms");
			server.shutdown();
			printSeparator();
		} catch (Exception e) {
			assert (false);
		}
		assert (true);
	}

	private void printSeparator() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out
				.println(" =============================================================================");
	}

	static public class MessageHandler extends RouteHandlerGeneric {
		long counter = 1;

		@Override
		protected void doPost(ChannelHandlerContext ctx, HttpRequest httpReq) {
			System.out.println("receiving message");
			if (counter > 10000) {

				if (failureCondition.equals("All")) {
					ctx.close();
				} else {
					if (failureCondition.equals("Latency")) {
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						if (failureCondition.equals("FailedConnexion")) {
							ctx.close();
						} else {
							if (failureCondition.equals("FailedIO")) {
								//ctx.fireExceptionCaught(new ChannelException());
							}
						}
					}
				}
			}
			counter++;

		}
	}
}
