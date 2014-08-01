package com.neverwinterdp.demandspike.yarn;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.app.AppContainer;
import com.neverwinterdp.hadoop.yarn.app.AppWorker;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeWorker implements AppWorker {
  protected static final Logger LOGGER = LoggerFactory.getLogger(DemandSpikeWorker.class);
  
  public void run(AppContainer appContainer) throws Exception {
    ApplicationMonitor appMonitor = new ApplicationMonitor() ;
    
    Map<String, String> props = appContainer.getConfig().conf ;
    MessageSender sender = new MessageSender() ;
    sender.run(); 
    System.out.println(appMonitor.toJSON());
  }
} 
