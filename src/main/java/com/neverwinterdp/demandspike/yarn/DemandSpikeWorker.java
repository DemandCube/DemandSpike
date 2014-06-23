package com.neverwinterdp.demandspike.yarn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.DemandSpikeJob;
import com.neverwinterdp.demandspike.DemandSpikeTask;
import com.neverwinterdp.hadoop.yarn.app.AppContainer;
import com.neverwinterdp.hadoop.yarn.app.AppWorker;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeWorker implements AppWorker {
  protected static final Logger LOGGER = LoggerFactory.getLogger(DemandSpikeWorker.class);
  
  public void run(AppContainer appContainer) throws Exception {
    ApplicationMonitor appMonitor = new ApplicationMonitor() ;
    DemandSpikeJob job = new DemandSpikeJob(appContainer.getConfig().conf) ;
    DemandSpikeTask task = job.createTask(appMonitor, "DemandSpikeTask") ;
    task.setLogger(LOGGER);
    task.run() ; 
    System.out.println(appMonitor.toJSON());
  }
} 
