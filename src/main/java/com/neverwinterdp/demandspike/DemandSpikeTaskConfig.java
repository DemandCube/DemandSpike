package com.neverwinterdp.demandspike;

import java.io.Serializable;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeTaskConfig implements Serializable {
  @Parameter(
    names = "--type", description = "Job type, either periodic or normal"
  )
  public String type = "normal";
  
  @Parameter(
    names = "--message-size", description = "The size of the message in bytes"
  )
  public int    messageSize = 1024 ;
  
  @Parameter(
    names = "--max-duration", description = "Maximum duration time of each task"
  )
  public long   maxDuration = 30 * 1000 ; // 30s
  
  @Parameter(
    names = "--max-num-of-message", description = "Maximum number of message for all tasks"
  )
  public long   maxNumOfMessage = 1000000; //1 million messages by default
  
  @Parameter(
    names = "--send-period", description = "Send period, use with the periodic task"
  )
  public long   sendPeriod = 100 ; // Every 100 ms
  
  @ParametersDelegate
  final public MessageDriverConfig driverConfig = new MessageDriverConfig() ;
  
  public DemandSpikeTask createTask(ApplicationMonitor appMonitor, String taskId) {
    DemandSpikeTask task = null ;
    if("periodic".equals(type)) {
      PeriodicTask ptask = new PeriodicTask() ;
      ptask.setSendPeriod(sendPeriod);
      task = ptask ;
    } else {
      task = new NormalTask() ;
    }
    task.setTaskId(taskId);
    task.setMaxDuration(maxDuration);
    task.setMaxNumOfMessage(maxNumOfMessage);
    
    MessageDriver driver = driverConfig.createDriver(appMonitor) ;
    task.setDriver(driver);
    
    MessageGenerator generator = new MessageGenerator() ;
    generator.setIdPrefix(task.getTaskId());
    generator.setMessageSize(messageSize);
    task.setMessageGenerator(generator);
    return task ;
  }
}