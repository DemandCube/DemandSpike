package com.neverwinterdp.demandspike;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.demandspike.job.send.MessageSenderConfig;
import com.neverwinterdp.demandspike.job.send.MessageSenderTask;
import com.neverwinterdp.server.gateway.MemberSelector;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJob implements Serializable {
  @ParametersDelegate
  final public MessageSenderConfig messageSenderConfig = new MessageSenderConfig() ;
  
  @ParametersDelegate
  final public ProblemSimulatorConfig problemConfig = new ProblemSimulatorConfig() ;
  
  @ParametersDelegate
  final public MemberSelector memberSelector = new MemberSelector();
  
  private long id ;
  
  public DemandSpikeJob() {
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
  
  public long getId() { return this.id ; }
  public void setId(long id) { this.id = id ; }
  
  public MessageSenderTask[] createTasks(ApplicationMonitor appMonitor) {
    MessageSenderTask[] task = new MessageSenderTask[messageSenderConfig.numOfTasks] ;
    for(int i = 0; i < task.length; i++) {
      task[i] = messageSenderConfig.createMessageSender(appMonitor, "task-" + (i + 1)) ;
    }
    return task ;
  }
}