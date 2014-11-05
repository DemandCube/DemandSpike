package com.neverwinterdp.demandspike.standalone;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.demandspike.HttpServerSimulator;
import com.neverwinterdp.demandspike.client.Monitor;
import com.neverwinterdp.demandspike.client.MonitorPrinter;

public class DemandSpikeUnitTest {
  private  HttpServerSimulator server  ;
  
  @Before
  public void setup() {
    //TODO: init the server
  }
  
  @After
  public void teardown() {
    server.shutdown() ;
  }
  
  @Test
  public void testDemandSpikeGET() throws Exception {
    //TODO: add the config parameters
    String[] args = {
        
    };
    DemandSpike demandspike = new DemandSpike(args) ;
    demandspike.start();
    demandspike.awaitTermination(10000, TimeUnit.MILLISECONDS);
    Monitor monitor = demandspike.getMergeMonitors() ;
    new MonitorPrinter().print(monitor) ;
    //TODO: create a config with 3 workers, split and send 100 GET 
  }
}
