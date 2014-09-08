package com.neverwinterdp.demandspike;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.neverwinterdp.server.module.ModuleProperties;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeClusterService extends AbstractService {
  private Logger logger ;
  
  @Inject
  private ModuleProperties moduleProperties; 
  
  @Inject
  private ApplicationMonitor appMonitor ;
  
  private DemandSpikeJobScheduler jobScheduler ;
  
  @Inject
  public void init(LoggerFactory factory) {
    logger = factory.getLogger(getClass()) ;
  }

  public boolean submit(DemandSpikeJob job, long timeout) throws InterruptedException {
    return jobScheduler.submit(job, timeout) ;
  }

  public DemandSpikeJobSchedulerInfo getSchedulerInfo() {
    return jobScheduler.getInfo() ;
  }
  
  public void start() throws Exception {
    logger.info("Start start()");
    jobScheduler = new DemandSpikeJobScheduler(appMonitor) ;
    jobScheduler.start();
    logger.info("Finish start()");
  }

  public void stop() {
    logger.info("Start stop()");
    if(jobScheduler != null) {
      jobScheduler.stop() ;
    }
    logger.info("Finish stop()");
  }
}