package com.neverwinterdp.demandspike.yarn;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.app.worker.AppWorker;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainer;
import com.neverwinterdp.message.Message;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeAppWorker implements AppWorker {
  protected static final Logger LOGGER = LoggerFactory.getLogger(DemandSpikeAppWorker.class);
  
  public void run(AppWorkerContainer appContainer) throws Exception {
    ApplicationMonitor appMonitor = new ApplicationMonitor() ;
    Map<String, String> props = appContainer.getConfig().yarnConf ;
    MessageSender sender = new MessageSender("127.0.0.1", 8080, 1000) ;
    for(int i = 0; i < 100; i++) {
      Message message = new Message("message " + i, "message data " + i, true) ;
      sender.send(message, 3000);
    }
    System.out.println(appMonitor.toJSON());
  }
} 
