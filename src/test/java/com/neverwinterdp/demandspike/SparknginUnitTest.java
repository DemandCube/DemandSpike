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

  @BeforeClass
  public static void setup() {
   /* server = new HttpServer();
    server.add("/message", new MessageHandler());
    server.setPort(7080);*/
  }

  @AfterClass
  public static void teardown() throws Exception {

  }

  @Test
  public void testSendingMillionMessages() {
    try {
      //server.startAsDeamon();
      System.out.println("Sending million messages");

      String[] args = { "run", "--target", "http://127.0.0.1:7080/message", "--protocol", "HTTP", "--method", "POST",
          "--input-data", this.data, "--maxRequests", "10000" };
      long startTime = System.currentTimeMillis();
      DemandSpike.main(args);
      long stopTime = System.currentTimeMillis();
      long elapsedTime = stopTime - startTime;
      System.out.println("Execution time " + elapsedTime + " ms");
      //server.shutdown();

    } catch (Exception e) {
      assert (false);
    }
    assert (true);
  }

  static public class MessageHandler extends RouteHandlerGeneric {
    long counter = 1;

    @Override
    protected void doPost(ChannelHandlerContext ctx, HttpRequest httpReq) {
      counter++;
    }
  }
}
