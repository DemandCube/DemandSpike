package com.neverwinterdp.demandspike.yarn.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorker;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainer;
import com.neverwinterdp.message.Message;

public class DemandSpikeAppWorker implements AppWorker {
  protected static final Logger LOGGER = LoggerFactory.getLogger(DemandSpikeAppWorker.class);
  
  public void run(AppWorkerContainer appContainer) throws Exception {
    MessageSender sender = null ;
    try {
      AppInfo config = appContainer.getConfig() ;
      DemandSpikeAppWorkerConfig demandSpikeWorkerConfig = new DemandSpikeAppWorkerConfig(config.yarnConf) ;
      sender = new MessageSender(demandSpikeWorkerConfig.getBrokerConnect(), 1000) ;
      long stopTime = System.currentTimeMillis() + demandSpikeWorkerConfig.getMaxDuration() ;
      for(int i = 0; i < demandSpikeWorkerConfig.getNumOfMessages(); i++) {
        Message message = new Message("message " + i, "message data " + i, true) ;
        message.getHeader().setTopic(demandSpikeWorkerConfig.getTopic());
        sender.send(message, 3000);
        if(i % 100 == 0) {
          if(System.currentTimeMillis() >= stopTime) break ;
        }
      }
    } finally {
      if(sender != null)  sender.waitAndClose(15000);
    }
  }
} 
