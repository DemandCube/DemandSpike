package com.neverwinterdp.demandspike.yarn.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.demandspike.job.DemandSpikeJob;
import com.neverwinterdp.demandspike.job.DemandSpikeJobConfig;
import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorker;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainer;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;


public class DemandSpikeAppWorker implements AppWorker {
  protected static final Logger LOGGER = LoggerFactory.getLogger(DemandSpikeAppWorker.class);
  
  public void run(AppWorkerContainer appContainer) throws Exception {
    MessageSender sender = null ;
    try {
      AppInfo appConfig = appContainer.getConfig() ;
      DemandSpikeJobConfig demandSpikeWorkerConfig = new DemandSpikeJobConfig(appConfig.yarnConf) ;
    
      List<String> connect = new ArrayList<String>();
	  
	  connect.add(demandSpikeWorkerConfig.getBrokerConnect());
	  Method method = Method.GET;
      ApplicationMonitor appMonitor = new ApplicationMonitor() ;
      DemandSpikeJob job = new DemandSpikeJob("sparkngin producer",appMonitor, demandSpikeWorkerConfig) ;
      job.run();
      ApplicationMonitorSnapshot snapshot = appMonitor.snapshot() ;
      Map<String, TimerSnapshot> timers = snapshot.getRegistry().getTimers() ;
      MetricFormater formater = new MetricFormater("  ") ;    
      System.out.println(formater.format(timers));
    } finally {
      if(sender != null)  sender.waitAndClose(15000);
    }
  }
} 
