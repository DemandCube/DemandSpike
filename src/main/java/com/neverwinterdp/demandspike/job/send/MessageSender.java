package com.neverwinterdp.demandspike.job.send;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.job.MessageSenderTask;
import com.neverwinterdp.server.gateway.ClusterGateway;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class MessageSender implements Runnable {
  protected static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);
  
  ApplicationMonitor appMonitor ;
  JobConfig config ;

  
  public MessageSender(ApplicationMonitor monitor, JobConfig config ) {
    this.appMonitor = monitor ;
    this.config = config ;

  }
  
  public void run() {
    ExecutorService taskExecutor = null ;
    try {
      taskExecutor = Executors.newFixedThreadPool((int)config.nMessages);
      MessageSenderTask[] task = config.createMessageSender(appMonitor) ;
      for(int i = 0; i < task.length; i++) {
        task[i].setLogger(LOGGER) ;
        taskExecutor.submit(task[i]) ;
      }
      taskExecutor.shutdown();
      taskExecutor.awaitTermination(config.maxDuration, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
    } catch(Exception ex) {
      LOGGER.error("Unexpected error", ex);
    } finally {
      if(taskExecutor != null) {
        taskExecutor.shutdownNow() ;
      }
    }
  }
}
