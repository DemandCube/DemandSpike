package com.neverwinterdp.demandspike;

import java.io.Serializable;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate ;
import com.neverwinterdp.server.client.CommandParams;
import com.neverwinterdp.server.client.MemberSelector;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJob implements Serializable {
  @Parameter(
    names = "--type", description = "Job type, either periodic or normal"
  )
  String type = "normal";
  
  @Parameter(
    names = "--num-of-task", description = "Number of tasks "
  )
  int    numOfTask = 1;
  
  @Parameter(
    names = "--num-of-thread", description = "Number of threads"
  )
  int    numOfThread = 1;
  
  @Parameter(
    names = "--message-size", description = "The size of the message in bytes"
  )
  int    messageSize = 1000 ;
  
  @Parameter(
    names = "--max-duration", description = "Maximum duration time of each task"
  )
  long   maxDuration = 60 * 60 * 1000 ; // 1 hour
  
  @Parameter(
    names = "--max-num-of-message", description = "Maximum number of message for all tasks"
  )
  long   maxNumOfMessage = 1000000; // 1 million messages by default
  
  @Parameter(
      names = "--send-period", description = "Send period, use with the periodic task"
  )
  long   sendPeriod = 100 ; // Every 100 ms
  
  @ParametersDelegate
  final public MessageDriverFactory driverFactory = new MessageDriverFactory() ;
  
  @ParametersDelegate
  final public MemberSelector memberSelector = new MemberSelector();
  
  public DemandSpikeJob() {
  }
  
  public DemandSpikeJob(CommandParams params) {
    JCommander jcommander = new JCommander(this) ;
    jcommander.setAcceptUnknownOptions(true);
    jcommander.parse(params.getArguments());
  }
  
  public void setJobType(String type) {
    this.type = type ;
  }
  
  public MessageDriverFactory getMessageDriverFactory() { return this.driverFactory ; }
  
  public DemandSpikeTask[] createTasks(ApplicationMonitor appMonitor) {
    long maxMessagePerTask = Math.round((double)maxNumOfMessage/numOfTask) ;
    DemandSpikeTask[] task = new DemandSpikeTask[numOfTask] ;
    for(int i = 0; i < task.length; i++) {
      task[i] = createTask() ;
      
      task[i].setTaskId("task-" + (i + 1));
      task[i].setMaxDuration(maxDuration);
      task[i].setMaxNumOfMessage(maxMessagePerTask);
      
      MessageDriver driver = driverFactory.createDriver(appMonitor) ;
      task[i].setDriver(driver);
      
      MessageGenerator generator = new MessageGenerator() ;
      generator.setIdPrefix(task[i].getTaskId());
      generator.setMessageSize(messageSize);
      task[i].setMessageGenerator(generator);
    }
    return task ;
  }
  
  private DemandSpikeTask createTask() {
    if("periodic".equals(type)) {
      PeriodicTask task = new PeriodicTask() ;
      task.setSendPeriod(sendPeriod);
      return task ;
    }
    return new NormalTask() ;
  }
}
