package com.neverwinterdp.demandspike;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DemandSpikeJobScheduler {
  private BlockingQueue<DemandSpikeJob> jobQueue = new LinkedBlockingQueue<DemandSpikeJob>() ;
  private SchedulerThread schedulerThread; 

  public boolean submit(DemandSpikeJob job, long timeout) throws InterruptedException {
    return jobQueue.offer(job, timeout, TimeUnit.MILLISECONDS) ;
  }
  
  public void start() {
    this.schedulerThread = new SchedulerThread() ;
    this.schedulerThread.start() ;
  }
  
  public void stop() {
    if(schedulerThread != null && schedulerThread.isAlive()) {
      schedulerThread.interrupt() ;
    }
  }
  
  public class SchedulerThread extends Thread {
    public void run() {
      ExecutorService taskExecutor = null ;
      try {
        DemandSpikeJob job = null ;
        while((job = jobQueue.take()) != null) {
          taskExecutor = Executors.newFixedThreadPool(job.numOfThread);
          DemandSpikeTask[] task = job.createTasks() ;
          for(int i = 0; i < task.length; i++) {
            taskExecutor.submit(task[i]) ;
          }
          taskExecutor.shutdown();
          taskExecutor.awaitTermination(job.maxDuration, TimeUnit.MILLISECONDS);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
        if(taskExecutor != null) {
          taskExecutor.shutdownNow() ;
        }
      }
    }
  }
}