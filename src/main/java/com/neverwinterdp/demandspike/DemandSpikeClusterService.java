package com.neverwinterdp.demandspike;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.neverwinterdp.server.module.ModuleProperties;
import com.neverwinterdp.server.service.AbstractService;
import com.neverwinterdp.util.LoggerFactory;

public class DemandSpikeClusterService extends AbstractService {
  private Logger logger ;
  
  @Inject
  private ModuleProperties moduleProperties; 
  
  private DemandSpikeJobScheduler jobScheduler ;
  
  @Inject
  public void init(LoggerFactory factory) {
    logger = factory.getLogger(getClass().getSimpleName()) ;
  }

  public boolean submit(DemandSpikeJob job, long timeout) throws InterruptedException {
    return jobScheduler.submit(job, timeout) ;
  }

  public void start() throws Exception {
    logger.info("Start start()");
    jobScheduler = new DemandSpikeJobScheduler() ;
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