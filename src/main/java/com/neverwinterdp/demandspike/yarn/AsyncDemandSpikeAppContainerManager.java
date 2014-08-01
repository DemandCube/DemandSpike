package com.neverwinterdp.demandspike.yarn ;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.hadoop.yarn.app.AppContainerConfig;
import com.neverwinterdp.hadoop.yarn.app.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.AppMonitor;
import com.neverwinterdp.hadoop.yarn.app.ContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.ContainerManager;
import com.neverwinterdp.hadoop.yarn.app.ContainerState;
import com.neverwinterdp.util.text.TabularPrinter;

public class AsyncDemandSpikeAppContainerManager implements ContainerManager {
  protected static final Logger LOGGER = LoggerFactory.getLogger(AsyncDemandSpikeAppContainerManager.class);
  private int numOfTasks = 3 ;
  
  public void onInit(AppMaster appMaster) {
    LOGGER.info("Start onInit(AppMaster appMaster)");
    Configuration conf = appMaster.getConfiguration() ;
    int instanceMemory  = conf.getInt("demandspike.instance.memory", 128) ;
    int instanceCores   = conf.getInt("demandspike.instance.core", 1) ;
    for (int i = 0; i < numOfTasks; i++) {
      ContainerRequest containerReq = 
          appMaster.createContainerRequest(0/*priority*/, instanceCores, instanceMemory);
      appMaster.add(containerReq) ;
    }
    LOGGER.info("Finish onInit(AppMaster appMaster)");
  }

  public void onAllocatedContainer(AppMaster appMaster, Container container) {
    try {
      LOGGER.info("onAllocateContainer(...), container id = " + container.getId());
      AppContainerConfig config = new AppContainerConfig(appMaster, container) ;
      config.setWorker(DemandSpikeWorker.class) ;
      config.conf.putAll(appMaster.getConfig().conf);
      appMaster.startContainer(container, config.toCommand()) ;
    } catch(Exception ex) {
      LOGGER.error("Start container error", ex);
    }
  }

  public void onCompleteContainer(AppMaster master, ContainerStatus status, ContainerInfo containerInfo) {
    try {
      AppMonitor monitor = master.getAppMonitor() ;
      int complete = monitor.getCompletedContainerCount().intValue() ;
      master.getAMRMClient().allocate(complete/(float)numOfTasks) ;
    } catch (Exception e) {
      LOGGER.error("onCompleteContainer() report error", e);
    }
    LOGGER.info("on complete container " + status.getContainerId());
  }

  public void onFailedContainer(AppMaster master, ContainerStatus status, ContainerInfo containerInfo) {
    LOGGER.info("on failed container " + status.getContainerId());
  }

  public void waitForComplete(AppMaster appMaster) {
    LOGGER.info("Start waitForComplete(AppMaster appMaster)");
    try {
      boolean finished = false ;
      while(!finished) {
        synchronized(this) {
          this.wait(500);
        } 
        AppMonitor monitor = appMaster.getAppMonitor() ;
        ContainerInfo[] cinfos = monitor.getContainerInfos() ;
        if(cinfos.length < numOfTasks)  continue ;
        finished = true; 
        for(ContainerInfo sel : cinfos) {
          if(!sel.getProgressStatus().getContainerState().equals(ContainerState.FINISHED)) {
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
    AppMonitor appMonitor = appMaster.getAppMonitor() ;
    ContainerInfo[] info = appMonitor.getContainerInfos() ;
    int[] colWidth = {20, 20, 20, 20} ;
    TabularPrinter printer = new TabularPrinter(System.out, colWidth) ;
    printer.header("Id", "Progress", "Error", "State");
    for(ContainerInfo sel : info) {
      printer.row(
        sel.getContainerId().getId(), 
        sel.getProgressStatus().getProgress(),
        sel.getProgressStatus().getError() != null,
        sel.getProgressStatus().getContainerState());
    }
    LOGGER.info("Finish onExit(AppMaster appMaster)");
  }
}