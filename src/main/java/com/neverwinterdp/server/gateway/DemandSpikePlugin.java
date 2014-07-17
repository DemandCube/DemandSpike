package com.neverwinterdp.server.gateway;

import com.neverwinterdp.demandspike.DemandSpikeClusterService;
import com.neverwinterdp.demandspike.DemandSpikeJob;
import com.neverwinterdp.demandspike.DemandSpikeJobSchedulerInfo;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.command.ServiceCommands;

@PluginConfig(name = "demandspike")
public class DemandSpikePlugin extends Plugin {
  public Object doCall(String commandName, CommandParams params) {
    if("submit".equals(commandName)) return submit(params) ;
    else if("status".equals(commandName)) return status(params) ;
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
  
  public ServiceCommandResult<DemandSpikeJobSchedulerInfo>[] status(CommandParams params) {
    MemberSelector memberSelector = new MemberSelector(params) ;
    return  status(memberSelector) ;
  }
  
  public ServiceCommandResult<DemandSpikeJobSchedulerInfo>[] status(MemberSelector memberSelector) {
    ServiceCommand<DemandSpikeJobSchedulerInfo> methodCall = 
        new ServiceCommands.MethodCall<DemandSpikeJobSchedulerInfo>("getSchedulerInfo") ;
    methodCall.setTargetService("DemandSpike", DemandSpikeClusterService.class.getSimpleName());
    return  memberSelector.execute(clusterClient, methodCall) ;
  }
}