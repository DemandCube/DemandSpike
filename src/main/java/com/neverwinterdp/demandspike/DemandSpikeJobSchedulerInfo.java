package com.neverwinterdp.demandspike;

import java.io.Serializable;
import java.util.List;

public class DemandSpikeJobSchedulerInfo implements Serializable {
  private List<DemandSpikeJob> waittingJobs ;
  private DemandSpikeJob runningJob ;
  
  public List<DemandSpikeJob> getWaittingJobs() { return waittingJobs; }
  public void setWaittingJobs(List<DemandSpikeJob> waittingJobs) { this.waittingJobs = waittingJobs; }
  
  public DemandSpikeJob getRunningJob() { return runningJob ; }
  public void setRunningJob(DemandSpikeJob runningJob) { this.runningJob = runningJob; }
}
