package com.neverwinterdp.demandspike;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.RouteHandlerGeneric;

public class SparknginUnitTest {
  String data = "{\"header\" : {\"version\" : 0.0,\"topic\" : \"metrics.consumer\",\"key\" : \"message-sender-task-0\",\"traceEnable\" : false,\"instructionEnable\" : false},\"data\" : {\"type\" : null,\"data\" : \"IkFBQUFBQU=\",\"serializeType\" : null},\"traces\" : null,\"instructions\" : null}";
  private static HttpServer server;
  static long counter = 1;
  private static String target = "178.62.89.248";
  //private static String target = "localhost";
  private static long maxRequests = 1000000;

  @BeforeClass
  public static void setup() {
    if (target.equals("localhost")) {
      server = new HttpServer();
      server.add("/message", new MessageHandler());
      server.setPort(7080);
      server.startAsDeamon();
    }
  }

  @AfterClass
  public static void teardown() throws Exception {
    if (target.equals("localhost")) {
      server.shutdown();
    }
  }

  @Test
  public void testSendingMillionMessages() {
    long startTime = System.currentTimeMillis();
    try {
      
      System.out.println("Sending million messages");
      String[] args = { "run", "--target", "http://" + target + ":7080/message", "--protocol", "HTTP", "--method",
          "POST", "--input-data", this.data, "--maxRequests", maxRequests+"", "--time", "300000", "--rate", "5000",};
      DemandSpike.main(args);
      

    } catch (Exception e) {
      System.out.println("Exception " + e.getMessage());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    System.out.println("Execution time " + elapsedTime + " ms");
    System.out.println("Messages sent " + maxRequests);
    assert (counter == maxRequests);
  }

  static public class MessageHandler extends RouteHandlerGeneric {

    @Override
    protected void doPost(ChannelHandlerContext ctx, HttpRequest httpReq) {
      counter++;
    }
  }
}
