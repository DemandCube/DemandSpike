package com.neverwinterdp.demandspike;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.server.gateway.CommandParams;
import com.neverwinterdp.server.gateway.MemberSelector;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class DemandSpikeJob implements Serializable {
  @Parameter(
    names = "--num-of-task", description = "Number of tasks "
  )
  public int    numOfTask = 1;
  
  @Parameter(
    names = "--num-of-thread", description = "Number of threads"
  )
  public int    numOfThread = 1;
  
  @ParametersDelegate
  final public DemandSpikeTaskConfig taskConfig = new DemandSpikeTaskConfig() ;
  
  @ParametersDelegate
  final public ProblemSimulatorConfig problemConfig = new ProblemSimulatorConfig() ;
  
  @ParametersDelegate
  final public MemberSelector memberSelector = new MemberSelector();
  
  private long id ;
  
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
  
  public long getId() { return this.id ; }
  public void setId(long id) { this.id = id ; }
  
  public DemandSpikeTask[] createTasks(ApplicationMonitor appMonitor) {
    DemandSpikeTask[] task = new DemandSpikeTask[numOfTask] ;
    for(int i = 0; i < task.length; i++) {
      task[i] = taskConfig.createTask(appMonitor, "task-" + (i + 1)) ;
    }
    return task ;
  }
}