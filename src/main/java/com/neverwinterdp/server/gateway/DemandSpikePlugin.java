package com.neverwinterdp.server.gateway;

import com.neverwinterdp.demandspike.DemandSpikeClusterService;
import com.neverwinterdp.demandspike.DemandSpikeJob;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.command.ServiceCommands;

@PluginConfig(name = "demandspike")
public class DemandSpikePlugin extends Plugin {
  public Object doCall(String commandName, CommandParams params) {
    ServiceCommandResult<?>[] results = null ;
    if("submit".equals(commandName)) results = submit(params) ;
    if(results != null) return results ;
    return null ;
  }

  public ServiceCommandResult<Boolean>[] submit(CommandParams params) {
    DemandSpikeJob job = new DemandSpikeJob(params) ;
    return submit(job, job.memberSelector.timeout) ;
  }
  
  public ServiceCommandResult<Boolean>[] submit(DemandSpikeJob job, long waitTime) {
    ServiceCommand<Boolean> methodCall = 
        new ServiceCommands.MethodCall<Boolean>("submit", job, waitTime) ;
    methodCall.setTargetService("DemandSpike", DemandSpikeClusterService.class.getSimpleName());
    return  job.memberSelector.execute(clusterClient, methodCall) ;
  }
}