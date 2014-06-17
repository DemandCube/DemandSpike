package com.neverwinterdp.demandspike.yarn ;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.AppContainerManager;
import com.neverwinterdp.hadoop.yarn.AppMaster;
import com.neverwinterdp.hadoop.yarn.AppMonitor;
import com.neverwinterdp.hadoop.yarn.ContainerInfo;
import com.neverwinterdp.server.ServerState;
import com.neverwinterdp.server.client.Cluster;
import com.neverwinterdp.server.client.MemberSelector;
import com.neverwinterdp.server.command.ServerCommandResult;

public class DemandSpikeAppContainerManager implements AppContainerManager {
  protected static final Logger LOGGER = LoggerFactory.getLogger(DemandSpikeAppContainerManager.class);
  
  private  Cluster cluster ;
  private boolean appStartSuccess = false;
  
  public void onInit(AppMaster appMaster) {
    Configuration conf = appMaster.getConfiguration() ;
    int instanceRequest = conf.getInt("demandspike.instance.request", 1) ;
    int instanceMemory  = conf.getInt("demandspike.instance.memory", 128) ;
    int instanceCores  = conf.getInt("demandspike.instance.core", 1) ;
    for (int i = 0; i < instanceRequest; i++) {
      ContainerRequest containerReq = 
          appMaster.createContainerRequest(0/*priority*/, instanceCores, instanceMemory);
      appMaster.add(containerReq) ;
    }
    StringBuilder commandBuilder = new StringBuilder() ;
    commandBuilder.
      append("java ").append("com.neverwinterdp.server.Server ").
      append(" -Pserver.name=demandspike ").
      append(" -Pserver.roles=demandspike") ;
    try {
      int allocatedContainers = 0 ;
      while (allocatedContainers < instanceRequest) {
        Thread.sleep(300);
        List<Container> containers = appMaster.getAllocatedContainers();
        LOGGER.info("Allocate " + containers.size() + " containers");
        for (Container container : containers) {
          ++allocatedContainers;
          appMaster.startContainer(container, commandBuilder.toString());
        }
      }
    } catch(Exception ex) {
      LOGGER.error("Error on allocate and start container", ex);
    }
    
    System.setProperty("hazelcast.logging.type", "none") ;
    cluster = new Cluster() ;
    MemberSelector memberSelector = new MemberSelector().setMemberRole("demandspike") ;
    appStartSuccess = cluster.waitForRunningMembers(memberSelector, instanceRequest, 60 * 1000) ;
    if(!appStartSuccess) {
      LOGGER.error("Failed to launch " + instanceRequest + " instances"); ;
    }
  }

  public void onAllocatedContainer(AppMaster master, Container container) {
  }

  public void onCompleteContainer(AppMaster master, ContainerStatus status, ContainerInfo containerInfo) {
    LOGGER.info("on complete container " + status.getContainerId());
  }

  public void onFailedContainer(AppMaster master, ContainerStatus status, ContainerInfo containerInfo) {
    LOGGER.info("on failed container " + status.getContainerId());
  }

  public void waitForComplete(AppMaster appMaster) {
    LOGGER.info("Start waitForComplete(AppMaster appMaster)");
    if(appStartSuccess) {
      synchronized(this) {
        try {
          this.wait();
        } catch (InterruptedException ex) {
          LOGGER.error("wait interruption: ", ex);
        }
      }
    } else {
      LOGGER.info("App is started with some error, terminate the app!!!");
    }
    LOGGER.info("Finish waitForComplete(AppMaster appMaster)");
  }

  public void onShutdownRequest(AppMaster appMaster)  {
    LOGGER.info("Start onShutdownRequest(AppMaster appMaster)");
    synchronized(this) {
      this.notify();
    }
    LOGGER.info("Finish onShutdownRequest(AppMaster appMaster)");
  }

  public void onExit(AppMaster appMaster) {
    LOGGER.info("Start onExit(AppMaster appMaster)");
    ServerCommandResult<ServerState>[] results = 
        cluster.server.exit(new MemberSelector().setMemberRole("demandspike")) ;
    System.out.println("Shutdown result");
    for(ServerCommandResult<ServerState> sel : results) {
      System.out.println(sel.getFromMember().toString() + ": " + sel.getResult() ) ;
    }
    System.out.println("End Shutdown result");
    cluster.close(); 

    AppMonitor appMonitor = appMaster.getAppMonitor() ;
    ContainerInfo[] info = appMonitor.getContainerInfos() ;
    for(ContainerInfo sel : info) {
      if(!"SUCCESS".equals(sel.getCompleteStatus())) {
        LOGGER.error("failed on container with command " + sel.getCommands());
      }
    }
    LOGGER.info("Finish onExit(AppMaster appMaster)");
  }
}