package com.neverwinterdp.demandspike;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.neverwinterdp.server.gateway.ClusterGateway;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJobScheduler {
  private ApplicationMonitor appMonitor ;
  private BlockingQueue<DemandSpikeJob> jobQueue = new LinkedBlockingQueue<DemandSpikeJob>() ;
  private JobSchedulerThread schedulerThread; 

  public DemandSpikeJobScheduler(ApplicationMonitor appMonitor) {
    this.appMonitor = appMonitor ;
  }
  
  public boolean submit(DemandSpikeJob job, long timeout) throws InterruptedException {
    return jobQueue.offer(job, timeout, TimeUnit.MILLISECONDS) ;
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
          jobRunner = new JobRunner(job) ;
          jobRunner.start(); 
          while(jobRunner.isAlive()) {
            Thread.sleep(100);
          }
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
      Map<String, ProblemSimulator> simulators = null ;
      ClusterGateway cluster = new ClusterGateway() ;
      try {
        cluster.connect();
        simulators = job.problemConfig.getProblemSimulators() ;
        for(ProblemSimulator simulator : simulators.values()) {
          simulator.onInit(cluster);
          simulator.start();
        }
        
        taskExecutor = Executors.newFixedThreadPool(job.numOfThread);
        DemandSpikeTask[] task = job.createTasks(appMonitor) ;
        for(int i = 0; i < task.length; i++) {
          taskExecutor.submit(task[i]) ;
        }
        taskExecutor.shutdown();
        boolean terminated = taskExecutor.awaitTermination(job.taskConfig.maxDuration, TimeUnit.MILLISECONDS);
        if(!terminated) {
          taskExecutor.shutdownNow() ;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch(Exception ex) {
        ex.printStackTrace(); 
      } finally {
        if(simulators != null) {
          for(ProblemSimulator simulator : simulators.values()) {
            simulator.stop();
          }
        }
        if(taskExecutor != null) {
          taskExecutor.shutdownNow() ;
        }
        cluster.close() ;
      }
    }
  }
}