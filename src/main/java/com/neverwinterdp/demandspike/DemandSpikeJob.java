package com.neverwinterdp.demandspike;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.server.client.CommandParams;
import com.neverwinterdp.server.client.MemberSelector;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJob implements Serializable {
  @Parameter(
    names = "--type", description = "Job type, either periodic or normal"
  )
  public String type = "normal";
  
  @Parameter(
    names = "--num-of-task", description = "Number of tasks "
  )
  public int    numOfTask = 1;
  
  @Parameter(
    names = "--num-of-thread", description = "Number of threads"
  )
  public int    numOfThread = 1;
  
  @Parameter(
    names = "--message-size", description = "The size of the message in bytes"
  )
  public int    messageSize = 1000 ;
  
  @Parameter(
    names = "--max-duration", description = "Maximum duration time of each task"
  )
  public long   maxDuration = 30 * 1000 ; // 30s
  
  @Parameter(
    names = "--max-num-of-message", description = "Maximum number of message for all tasks"
  )
  public long   maxNumOfMessage = 1000000; // 1 million messages by default
  
  @Parameter(
      names = "--send-period", description = "Send period, use with the periodic task"
  )
  public long   sendPeriod = 100 ; // Every 100 ms
  
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
  
  public DemandSpikeJob(Map<String, String> props) {
    List<String> holder = new ArrayList<String>() ;
    for(Map.Entry<String, String> entry : props.entrySet()) {
      String key = entry.getKey() ;
      if(key.startsWith("demandspike.job.")) {
        key =  key.substring("demandspike.job.".length()) ;
        holder.add("--" + key);
        holder.add(entry.getValue()) ;
      }
    }
    String[] args = holder.toArray(new String[holder.size()]) ;
    new JCommander(this, args) ;
  }
  
  public void setJobType(String type) {
    this.type = type ;
  }
  
  public MessageDriverFactory getMessageDriverFactory() { return this.driverFactory ; }
  
  public DemandSpikeTask[] createTasks(ApplicationMonitor appMonitor) {
    DemandSpikeTask[] task = new DemandSpikeTask[numOfTask] ;
    for(int i = 0; i < task.length; i++) {
      task[i] = createTask(appMonitor, "task-" + (i + 1)) ;
    }
    return task ;
  }
  
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
    
    MessageDriver driver = driverFactory.createDriver(appMonitor) ;
    task.setDriver(driver);
    
    MessageGenerator generator = new MessageGenerator() ;
    generator.setIdPrefix(task.getTaskId());
    generator.setMessageSize(messageSize);
    task.setMessageGenerator(generator);
    return task ;
  }
}
