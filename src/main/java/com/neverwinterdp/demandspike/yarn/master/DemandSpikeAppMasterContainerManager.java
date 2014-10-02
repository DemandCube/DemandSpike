package com.neverwinterdp.demandspike.yarn.master ;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterContainerManager;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterMonitor;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerState;
import com.neverwinterdp.util.text.TabularPrinter;

public class DemandSpikeAppMasterContainerManager implements AppMasterContainerManager {
  protected static final Logger LOGGER = LoggerFactory.getLogger(DemandSpikeAppMasterContainerManager.class);
  
  public void onInit(AppMaster appMaster) {
  }
  
  public void onRequestContainer(AppMaster appMaster) {
    LOGGER.info("Start onInit(AppMaster appMaster)");
    Configuration conf = appMaster.getConfiguration() ;
    int instanceMemory  = conf.getInt("demandspike.instance.memory", 128) ;
    int instanceCores   = conf.getInt("demandspike.instance.core", 1) ;
    int numOfTasks = 3 ;
    for (int i = 0; i < numOfTasks; i++) {
      ContainerRequest containerReq = 
          appMaster.createContainerRequest(0/*priority*/, instanceCores, instanceMemory);
      appMaster.add(containerReq) ;
    }
    try {
      int allocatedContainer = 0 ;
      while(allocatedContainer < numOfTasks) {
        Thread.sleep(1000);
        AllocateResponse response = appMaster.getAMRMClient().allocate((float)allocatedContainer/numOfTasks);
        Resource resource = response.getAvailableResources() ;
        LOGGER.info("getAllocatedContainers() Avaiable Cores: " + resource.getVirtualCores());
        LOGGER.info("getAllocatedContainers() Avaiable Memory: " + resource.getMemory());
        
        List<Container> containers = response.getAllocatedContainers() ;
        LOGGER.info("Allocated " + containers.size() + " containers");
        for(Container container : containers) {
          appMaster.startContainer(container) ;
          allocatedContainer++ ;
        }
      }
    } catch (Exception e) {
      LOGGER.error("Start container error", e);
    }
    LOGGER.info("Finish onInit(AppMaster appMaster)");
  }

  public void onAllocatedContainer(AppMaster master, Container container) {
  }

  public void onCompleteContainer(AppMaster master, ContainerStatus status, AppWorkerContainerInfo containerInfo) {
    LOGGER.info("on complete container " + status.getContainerId());
  }

  public void onFailedContainer(AppMaster master, ContainerStatus status, AppWorkerContainerInfo containerInfo) {
    LOGGER.info("on failed container " + status.getContainerId());
  }

  public void waitForComplete(AppMaster appMaster) {
    LOGGER.info("Start waitForComplete(AppMaster appMaster)");
    AppMasterMonitor monitor = appMaster.getAppMonitor() ;
    AppWorkerContainerInfo[] cinfos = monitor.getContainerInfos() ;
    
    try {
      boolean finished = false ;
      while(!finished) {
        synchronized(this) {
          this.wait(500);
        } 
        finished = true; 
        for(AppWorkerContainerInfo sel : cinfos) {
          if(!sel.getProgressStatus().getContainerState().equals(AppWorkerContainerState.FINISHED)) {
            finished = false ;
            break ;
          }
        }
      }
    } catch (InterruptedException ex) {
      LOGGER.error("wait interruption: ", ex);
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
    AppMasterMonitor appMonitor = appMaster.getAppMonitor() ;
    AppWorkerContainerInfo[] info = appMonitor.getContainerInfos() ;
    int[] colWidth = {20, 20, 20, 20} ;
    TabularPrinter printer = new TabularPrinter(System.out, colWidth) ;
    printer.header("Id", "Progress", "Error", "State");
    for(AppWorkerContainerInfo sel : info) {
      printer.row(
        sel.getContainerId(), 
        sel.getProgressStatus().getProgress(),
        sel.getProgressStatus().getError() != null,
        sel.getProgressStatus().getContainerState());
    }
    LOGGER.info("Finish onExit(AppMaster appMaster)");
  }
}