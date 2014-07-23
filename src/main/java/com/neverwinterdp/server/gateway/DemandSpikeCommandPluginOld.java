package com.neverwinterdp.server.gateway;

import com.neverwinterdp.demandspike.DemandSpikeClusterService;
import com.neverwinterdp.demandspike.DemandSpikeJob;
import com.neverwinterdp.demandspike.DemandSpikeJobSchedulerInfo;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommands;

//@CommandPluginConfig(name = "demandspike")
public class DemandSpikeCommandPluginOld extends CommandPlugin {
  public DemandSpikeCommandPluginOld() {
    add("submit", new submit()) ;
    add("scheduler", new scheduler()) ;
  }
  
  static public class submit implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      DemandSpikeJob job = new DemandSpikeJob() ;
      command.mapAll(job);
      ServiceCommand<Boolean> methodCall = 
          new ServiceCommands.MethodCall<Boolean>("submit", job, command.getMemberSelector().timeout) ;
      methodCall.setTargetService("DemandSpike", DemandSpikeClusterService.class.getSimpleName());
      return  command.getMemberSelector().execute(clusterClient, methodCall) ;
    }
  }
 
  static public class scheduler implements SubCommandExecutor {
    public Object execute(ClusterClient clusterClient, Command command) throws Exception {
      ServiceCommand<DemandSpikeJobSchedulerInfo> methodCall = 
          new ServiceCommands.MethodCall<DemandSpikeJobSchedulerInfo>("getSchedulerInfo") ;
      methodCall.setTargetService("DemandSpike", DemandSpikeClusterService.class.getSimpleName());
      return  command.getMemberSelector().execute(clusterClient, methodCall) ;
    }
  }
}