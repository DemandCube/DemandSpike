package com.neverwinterdp.demandspike.standalone;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.neverwinterdp.demandspike.DemandSpikeConfig;
import com.neverwinterdp.demandspike.client.Monitor;

public class DemandSpike {
  private DemandSpikeConfig config ;
  private DemandSpikeWorker[] workers ;
  private ExecutorService executorService ;
  
  public DemandSpike(String[] args) {
    //TODO: use the jcommander to parse the config
    
    //TODO: create and init the number of worker according to the config
  
    //TODO: init the executor service
  }
  
  public Monitor[] getWorkerMonitors() {
    //TODO: create an array, get the monitor from the worker and return
    return null ;
  }
  
  public Monitor getMergeMonitors() {
    //TODO: merge the worker monitor and return
    return null ;
  }
  
  public void start() {
    //TODO: make sure that the workers are not started
    
    //TODO: use the executor service to run the worker
    
    //TODO: wait the executor until it finishs
  }

  public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    executorService.awaitTermination(timeout, unit) ;
  }
  
  public void stop() {
    //TODO: check and call executorService cancel to stop the worker
  }
  
  public void shutdown() {
    //TODO: call shutdown of the workers
  }
  
  static public void main(String[] args) throws Exception {
    
  }
}
