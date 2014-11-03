package com.neverwinterdp.demandspike;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;

import com.neverwinterdp.demandspike.client.DemandSpikeClient;
import com.neverwinterdp.demandspike.client.MonitorPrinter;

public class DemandSpikeClientUnitTest {
  /**
   * This test should test the GET and POST method. It should send few request to the the server and expect to get all
   * the response back
   * @throws Exception
   */
  @Test
  public void testSend() throws Exception {
    int NUM_REQUEST = 10 ;
    HttpServerSimulator server = new HttpServerSimulator(3 /*worker*/, 0 /*min process time*/, 1000/*max process time*/) ;
    server.start();
    DemandSpikeClient client = new DemandSpikeClient("127.0.0.1", 7080, "/message", 100, true) ;
    for(int i = 0; i < NUM_REQUEST; i++) {
      String key = "key-" + i ;
      byte[] data = HttpServerSimulator.newPayload(key, null, new byte[32]) ;
      if(i % 2 == 0) {
        Map<String, String> params = new HashMap<String, String>() ;
        params.put("data", new String(data)) ;
        client.sendGet("key-" + i, params, /*timeout*/1000);
      } else {
        client.sendPost("key-" + i, data, /*timeout*/1000);
      }
    }
    client.waitAndClose(5000);
    server.shutdown(); 
    Assert.assertEquals(NUM_REQUEST/2, server.handler.getCount);
    Assert.assertEquals(NUM_REQUEST/2, server.handler.postCount);
    
    System.out.println("testSend()");
    System.out.println("***************************************************************");
    new MonitorPrinter().print(client.getMonitor()) ;
  }

  /**
   * This test should test the timeout or overload request. The client should send an amount of the request to the server
   * that the server cannot handle and it result an timeout which the client cannot get the response in a period of time
   * @throws Exception
   */
  @Test
  public void testClientLimitTimeoutException() throws Exception {
    //Create a server that handle the request between 500 - 1000ms, slow process
    HttpServerSimulator server = new HttpServerSimulator(1 /*worker*/, 500 /*min process time*/, 1000/*max process time*/) ;
    server.start() ;
    //Allow the client send max 5 request at the same time
    DemandSpikeClient client = new DemandSpikeClient("127.0.0.1", 7080, "/message", 5, true) ;
    int NUM_REQUEST = 10;
    for(int i = 0; i < NUM_REQUEST; i++) {
      try {
        String key = "key-" + i ;
        byte[] data = HttpServerSimulator.newPayload(key, null, new byte[32]) ;
        if(i % 2 == 0) {
          Map<String, String> params = new HashMap<String, String>() ;
          params.put("data", new String(data)) ;
          client.sendGet("key-" + i, params, /*timeout*/100);
        } else {
          client.sendPost("key-" + i, data, /*timeout*/100);
        }
      } catch(TimeoutException ex) {
      }
    }
    client.waitAndClose(5000);
    server.shutdown() ;
    Assert.assertTrue(client.getMonitor().clientLimitTimeoutCount() > 0);
    System.out.println("testTimeoutException()");
    System.out.println("***************************************************************");
    new MonitorPrinter().print(client.getMonitor()) ;
  }
  
  /**
   * This test should simulate the case that the client cannot reach the server due to the network problem or the server
   * is down.
   * @throws Exception
   */
  @Test
  public void testCloseChannelException() throws Exception {
    HttpServerSimulator server = new HttpServerSimulator(3 /*worker*/, 0 /*min process time*/, 1000/*max process time*/) ;
    server.start();
    DemandSpikeClient client = new DemandSpikeClient("127.0.0.1", 7080, "/message", 5, true) ;
    server.shutdown() ;
    Thread.sleep(500);
    String key = "key" ;
    byte[] data = HttpServerSimulator.newPayload(key, null, new byte[32]) ;
    client.sendPost(key, data, /*timeout*/100);
    client.waitAndClose(1000);
    Assert.assertTrue(client.getMonitor().closeChannelExceptionCount() > 0);
    System.out.println("testConnectionTimeoutException()");
    System.out.println("***************************************************************");
    new MonitorPrinter().print(client.getMonitor()) ;
  }
  
  /**
   * This test should simulate the case that the server is too busy or take too much time to process the request 
   * and the client cannot wait for the response
   * @throws Exception
   */
  @Test
  public void testTimeoutException() throws Exception {
    //Create a server with long process time
    HttpServerSimulator server = new HttpServerSimulator(3 /*worker*/, 10000 /*min process time*/, 15000/*max process time*/) ;
    server.start();
    DemandSpikeClient client = new DemandSpikeClient("127.0.0.1", 7080, "/message", 10, true) ;
    int NUM_REQUEST = 10;
    for(int i = 0; i < NUM_REQUEST; i++) {
      try {
        String key = "key-" + i ;
        byte[] data = HttpServerSimulator.newPayload(key, null, new byte[32]) ;
        if(i % 2 == 0) {
          Map<String, String> params = new HashMap<String, String>() ;
          params.put("data", new String(data)) ;
          client.sendGet("key-" + i, params, /*timeout*/100);
        } else {
          client.sendPost("key-" + i, data, /*timeout*/100);
        }
      } catch(TimeoutException ex) {
      }
    }
    client.waitAndClose(1000);
    server.shutdown() ;
    Assert.assertTrue(client.getMonitor().timeoutCount() > 0);
  }
}
