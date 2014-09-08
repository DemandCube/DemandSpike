package com.neverwinterdp.demandspike.job.scheduler;

import java.io.Serializable;
import java.util.List;

import com.neverwinterdp.demandspike.job.DemandSpikeJob;

public class DemandSpikeJobSchedulerInfo implements Serializable {
  private List<DemandSpikeJob> waittingJobs ;
  private List<DemandSpikeJob> finishedJobs ;
  private DemandSpikeJob runningJob ;
  
  public List<DemandSpikeJob> getWaittingJobs() { return waittingJobs; }
  public void setWaittingJobs(List<DemandSpikeJob> waittingJobs) { this.waittingJobs = waittingJobs; }
  
  public DemandSpikeJob getRunningJob() { return runningJob ; }
  public void setRunningJob(DemandSpikeJob runningJob) { this.runningJob = runningJob; }
  
  public List<DemandSpikeJob> getFinishedJobs() { return finishedJobs; }
  
  public void setFinishedJobs(List<DemandSpikeJob> finishedJobs) { this.finishedJobs = finishedJobs; }
}
