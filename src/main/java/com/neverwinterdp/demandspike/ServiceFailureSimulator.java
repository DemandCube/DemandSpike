package com.neverwinterdp.demandspike;

import java.util.Timer;
import java.util.TimerTask;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.neverwinterdp.server.cluster.ClusterClient;
import com.neverwinterdp.server.cluster.ClusterMember;
import com.neverwinterdp.server.command.ServiceCommandResult;
import com.neverwinterdp.server.command.ServiceCommands;
import com.neverwinterdp.server.gateway.ClusterGateway;
import com.neverwinterdp.server.gateway.MemberSelector;
import com.neverwinterdp.server.service.ServiceRegistration;
import com.neverwinterdp.util.JSONSerializer;

public class ServiceFailureSimulator implements ProblemSimulator {
  @Parameter(names = "--problem", description = "Simulator problem name")
  private String  problem ;
  @Parameter(names = "--description", description = "Simulator problem description")
  private String  description ;
  
  @ParametersDelegate
  final public MemberSelector memberSelector = new MemberSelector();
  @Parameter(names = "--module", required=true, description = "Target module")
  private String  module ;
  @Parameter(names = "--service-id", required=true, description = "Target service id")
  private String  serviceId ;
  @Parameter(names = "--period", description = "repeat simulation period")
  private long    period = 30000;
  @Parameter(names = "--failure-time", description = "repeat simulation period")
  private long    faillureTime = 1000;
  
  private ClusterGateway cluster ;
  private Timer   timer ;
  
  public void onInit(ClusterGateway cluster) {
    this.cluster = cluster ;
  }
  
  public void start() {
    timer = new Timer() ;
    timer.schedule(new SimmulationTask(), 1000, period);;
  }

  public void stop() {
    timer.cancel();
    timer.purge() ;
    timer = null ;
  }
  
  public class SimmulationTask extends TimerTask {
    public void run() {
      if(cluster == null) {
        System.out.println("Cluster is not available!!!");
        return ;
      }
      try {
        ClusterClient client = cluster.getClusterClient() ;
        ClusterMember member = memberSelector.selectRandomMember(client) ;
        ServiceCommands.Stop stop = new ServiceCommands.Stop() ;
        stop.setTargetService(module, serviceId);
        ServiceCommandResult<ServiceRegistration> stopResult = client.execute(stop, member) ;
        System.out.println("ServiceFailureSimulator: stop " + serviceId + "!!!");
        System.out.println(JSONSerializer.INSTANCE.toString(stopResult)) ;
        Thread.sleep(faillureTime);
        ServiceCommands.Start start = new ServiceCommands.Start() ;
        start.setTargetService(module, serviceId);
        ServiceCommandResult<ServiceRegistration> startResult = client.execute(start, member) ;
        System.out.println("ServiceFailureSimulator: start " + serviceId + "!!!");
        System.out.println(JSONSerializer.INSTANCE.toString(startResult)) ;
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}