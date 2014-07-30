package com.neverwinterdp.demandspike.job;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.neverwinterdp.demandspike.job.config.DemandSpikeJob;
import com.neverwinterdp.server.shell.Shell;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJobScheduler {
  private AtomicLong idTracker = new AtomicLong() ;
  private ApplicationMonitor appMonitor ;
  private BlockingQueue<DemandSpikeJob> jobQueue = new LinkedBlockingQueue<DemandSpikeJob>() ;
  private Map<String, DemandSpikeJob> finishedJobs = new LinkedHashMap<String, DemandSpikeJob>() ;
  private DemandSpikeJob  runningJob = null ;
  private JobSchedulerThread schedulerThread; 

  public DemandSpikeJobScheduler(ApplicationMonitor appMonitor) {
    this.appMonitor = appMonitor ;
  }
  
  public boolean submit(DemandSpikeJob job, long timeout) throws InterruptedException {
    job.setId(Long.toString(idTracker.incrementAndGet()));
    return jobQueue.offer(job, timeout, TimeUnit.MILLISECONDS) ;
  }
  
  public List<DemandSpikeJob> getWaittingJobs() {
    List<DemandSpikeJob> holder = new ArrayList<DemandSpikeJob>() ;
    Iterator<DemandSpikeJob> i = jobQueue.iterator() ;
    while(i.hasNext()) holder.add(i.next()) ;
    return holder ;
  }
  
  public List<DemandSpikeJob> getfinishedJobs() {
    List<DemandSpikeJob> holder = new ArrayList<DemandSpikeJob>() ;
    Iterator<DemandSpikeJob> i = finishedJobs.values().iterator() ;
    while(i.hasNext()) holder.add(i.next()) ;
    return holder ;
  }
  
  public DemandSpikeJob getRunningJob() { return this.runningJob  ; }
  
  public DemandSpikeJobSchedulerInfo getInfo() {
    DemandSpikeJobSchedulerInfo info = new DemandSpikeJobSchedulerInfo() ;
    info.setRunningJob(getRunningJob());
    info.setWaittingJobs(getWaittingJobs());
    info.setFinishedJobs(getfinishedJobs());
    return info ;
  }
  
  public void start() {
    this.schedulerThread = new JobSchedulerThread() ;
    this.schedulerThread.start() ;
  }
  
  public void stop() {
    if(schedulerThread != null && schedulerThread.isAlive()) {
      schedulerThread.interrupt() ;
    }
  }
  
  public class JobSchedulerThread extends Thread {
    public void run() {
      DemandSpikeJobRunner jobRunner = null ;
      DemandSpikeJob job = null ;
      try {
        while((job = jobQueue.take()) != null) {
          runningJob = job ;
          jobRunner = new DemandSpikeJobRunner(job) ;
          jobRunner.start(); 
          while(jobRunner.isAlive()) {
            Thread.sleep(100);
          }
          finishedJobs.put(runningJob.getId(), runningJob) ;
          runningJob = null;
        }
      } catch (InterruptedException e) {
        if(jobRunner != null) jobRunner.interrupt();
      }
    }
  }
  
  public class DemandSpikeJobRunner extends Thread {
    DemandSpikeJob job ;
    
    public DemandSpikeJobRunner(DemandSpikeJob job) {
      this.job = job ;
    }
    
    public void run() {
      Shell shell = new Shell() ;
      try {
        shell.getShellContext().connect();
        ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
        PrintStream pout = new PrintStream(bout) ;
        shell.getShellContext().console().setPrintStream(pout, System.out);
        shell.evalJScript(job.getScript(), job.getScriptProperties()) ;
        job.setOutputAttribute("consoleOutput", new String(bout.toByteArray()));
      } catch(Throwable t) {
        job.setOutputAttribute("error", t);
        t.printStackTrace(); 
      } finally {
        shell.close() ;
      }
    }
  }
}