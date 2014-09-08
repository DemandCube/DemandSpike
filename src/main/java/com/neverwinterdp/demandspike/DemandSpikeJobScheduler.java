package com.neverwinterdp.demandspike;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.neverwinterdp.demandspike.job.send.MessageSenderTask;
import com.neverwinterdp.server.gateway.ClusterGateway;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJobScheduler {
  private AtomicLong idTracker = new AtomicLong() ;
  private ApplicationMonitor appMonitor ;
  private BlockingQueue<DemandSpikeJob> jobQueue = new LinkedBlockingQueue<DemandSpikeJob>() ;
  private DemandSpikeJob  runningJob = null ;
  private JobSchedulerThread schedulerThread; 

  public DemandSpikeJobScheduler(ApplicationMonitor appMonitor) {
    this.appMonitor = appMonitor ;
  }
  
  public boolean submit(DemandSpikeJob job, long timeout) throws InterruptedException {
    job.setId(idTracker.incrementAndGet());
    return jobQueue.offer(job, timeout, TimeUnit.MILLISECONDS) ;
  }
  
  public List<DemandSpikeJob> getWaittingJobs() {
    List<DemandSpikeJob> holder = new ArrayList<DemandSpikeJob>() ;
    Iterator<DemandSpikeJob> i = jobQueue.iterator() ;
    while(i.hasNext()) holder.add(i.next()) ;
    return holder ;
  }
  
  public DemandSpikeJob getRunningJob() { return this.runningJob  ; }
  
  public DemandSpikeJobSchedulerInfo getInfo() {
    DemandSpikeJobSchedulerInfo info = new DemandSpikeJobSchedulerInfo() ;
    info.setRunningJob(getRunningJob());
    info.setWaittingJobs(getWaittingJobs());
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
      JobRunner jobRunner = null ;
      DemandSpikeJob job = null ;
      try {
        while((job = jobQueue.take()) != null) {
          runningJob = job ;
          jobRunner = new JobRunner(job) ;
          jobRunner.start(); 
          while(jobRunner.isAlive()) {
            Thread.sleep(100);
          }
          runningJob = null;
        }
      } catch (InterruptedException e) {
        if(jobRunner != null) jobRunner.interrupt();
      }
    }
  }
  
  public class JobRunner extends Thread {
    DemandSpikeJob job ;
    
    public JobRunner(DemandSpikeJob job) {
      this.job = job ;
    }
    
    public void run() {
      ExecutorService taskExecutor = null ;
      ClusterGateway cluster = new ClusterGateway() ;
      try {
        cluster.connect();


        
        taskExecutor = Executors.newFixedThreadPool(job.messageSenderConfig.numOfProcesses);
        MessageSenderTask[] task = job.createTasks(appMonitor) ;
        for(int i = 0; i < task.length; i++) {
          taskExecutor.submit(task[i]) ;
        }
        taskExecutor.shutdown();
        boolean terminated = taskExecutor.awaitTermination(job.messageSenderConfig.maxDuration, TimeUnit.MILLISECONDS);
        if(!terminated) {
          taskExecutor.shutdownNow() ;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch(Exception ex) {
        ex.printStackTrace(); 
      } finally {

        if(taskExecutor != null) {
          taskExecutor.shutdownNow() ;
        }
        cluster.close() ;
      }
    }
  }
}