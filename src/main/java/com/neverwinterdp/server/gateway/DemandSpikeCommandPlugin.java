package com.neverwinterdp.server.gateway;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.demandspike.DemandSpikeJobSchedulerInfo;
import com.neverwinterdp.demandspike.job.DemandSpikeJobService;
import com.neverwinterdp.demandspike.job.config.DemandSpikeJob;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommands;
import com.neverwinterdp.util.IOUtil;
import com.neverwinterdp.util.JSONSerializer;
import com.neverwinterdp.util.text.StringUtil;

@CommandPluginConfig(name = "demandspike")
public class DemandSpikeCommandPlugin extends CommandPlugin {
  public DemandSpikeCommandPlugin() {
    add("submit", new submit()) ;
    add("scheduler", new scheduler()) ;
  }
  
  static public class submit implements SubCommandExecutor {
    @Parameter(names = {"-f", "--file"} , description = "The DemandSpikeJob in a json file")
    private String file ;
    
    @Parameter(description = "The DemandSpikeJob in a json format")
    private List<String> jsonData  = new ArrayList<String>() ;
    
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      command.mapAll(this);
      String json = null ;
      if(file != null) {
        json = IOUtil.getFileContentAsString(file) ;
      } else {
        json = StringUtil.join(jsonData, " ") ;
      }
      System.out.println("JSON INPUT " + json) ;
      DemandSpikeJob job = JSONSerializer.INSTANCE.fromString(json, DemandSpikeJob.class) ;
      ServiceCommand<Boolean> methodCall = 
          new ServiceCommands.MethodCall<Boolean>("submit", job, command.getMemberSelector().timeout) ;
      methodCall.setTargetService("DemandSpike", DemandSpikeJobService.class.getSimpleName());
      return  command.getMemberSelector().execute(clusterClient, methodCall) ;
    }
  }
 
  static public class scheduler implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServiceCommand<DemandSpikeJobSchedulerInfo> methodCall = 
          new ServiceCommands.MethodCall<DemandSpikeJobSchedulerInfo>("getSchedulerInfo") ;
      methodCall.setTargetService("DemandSpike", DemandSpikeJobService.class.getSimpleName());
      return command.getMemberSelector().execute(clusterClient, methodCall) ;
    }
  }
}