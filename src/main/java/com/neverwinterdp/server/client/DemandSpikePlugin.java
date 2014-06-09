package com.neverwinterdp.server.client;

import com.neverwinterdp.demandspike.DemandSpikeClusterService;
import com.neverwinterdp.demandspike.DemandSpikeJob;
import com.neverwinterdp.server.command.ServiceCommand;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.command.ServiceCommands;
import com.neverwinterdp.util.JSONSerializer;

@PluginConfig(name = "demandspike")
public class DemandSpikePlugin extends Plugin {
  public String call(String json) {
    try {
      CommandParams params = JSONSerializer.INSTANCE.fromString(json, CommandParams.class) ;
      String commandName = params.getString("_commandName") ;
      ServiceCommandResult<?>[] results = null ;
      if("submit".equals(commandName)) results = submit(params) ;
      if(results != null) {
        return JSONSerializer.INSTANCE.toString(results) ;
      }
      return "{ 'success': false, 'message': 'unknown command'}" ;
    } catch(Throwable t) {
      t.printStackTrace(); 
      throw t ;
    }
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