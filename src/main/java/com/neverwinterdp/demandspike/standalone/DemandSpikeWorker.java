package com.neverwinterdp.demandspike.standalone;

import com.neverwinterdp.demandspike.DemandSpikeConfig;
import com.neverwinterdp.demandspike.client.DemandSpikeClient;
import com.neverwinterdp.demandspike.client.Monitor;

public class DemandSpikeWorker implements Runnable {
  private DemandSpikeConfig config ;
  private DemandSpikeClient client ;
  
  public DemandSpikeWorker(DemandSpikeConfig config) {
    this.config = config ;
    //TODO: init the client
  }
  
  public Monitor getMonitor() {
    //TODO: return the monitor in the client
    return null ;
  }
  
  @Override
  public void run() {
    //TODO: run the loop to send the request to the server according to the config
  }

  public void shutdown() {
    //TODO: close the client , release the resource
  }
}
