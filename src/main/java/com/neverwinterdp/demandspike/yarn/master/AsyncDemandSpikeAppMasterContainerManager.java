package com.neverwinterdp.demandspike.yarn.master ;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neverwinterdp.demandspike.yarn.worker.DemandSpikeAppWorker;
import com.neverwinterdp.hadoop.yarn.app.AppInfo;
import com.neverwinterdp.hadoop.yarn.app.master.AppMaster;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterContainerManager;
import com.neverwinterdp.hadoop.yarn.app.master.AppMasterMonitor;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerInfo;
import com.neverwinterdp.hadoop.yarn.app.worker.AppWorkerContainerState;
import com.neverwinterdp.util.JSONSerializer;
import com.neverwinterdp.util.text.TabularPrinter;

public class AsyncDemandSpikeAppMasterContainerManager implements AppMasterContainerManager {
  protected static final Logger LOGGER = LoggerFactory.getLogger(AsyncDemandSpikeAppMasterContainerManager.class);
  
  public void onInit(AppMaster appMaster) {
  }
  
  public void onRequestContainer(AppMaster appMaster) {
    LOGGER.info("Start onRequestContainer(AppMaster appMaster)");
    AppInfo appConfig = appMaster.getAppInfo() ;
    appConfig.setWorkerByType(DemandSpikeAppWorker.class) ;
    System.out.println("AppInfo: " + JSONSerializer.INSTANCE.toString(appConfig));
    for (int i = 0; i < appConfig.appNumOfWorkers; i++) {
      ContainerRequest containerReq = 
          appMaster.createContainerRequest(0/*priority*/, appConfig.workerNumOfCore, appConfig.workerMaxMemory);
      appMaster.add(containerReq) ;
    }
    LOGGER.info("Finish onRequestContainer(AppMaster appMaster)");
  }

  public void onAllocatedContainer(AppMaster appMaster, Container container) {
    try {
      LOGGER.info("onAllocateContainer(...), container id = " + container.getId());
      appMaster.startContainer(container) ;
    } catch(Exception ex) {
      LOGGER.error("Start container error", ex);
    }
  }

  public void onCompleteContainer(AppMaster master, ContainerStatus status, AppWorkerContainerInfo containerInfo) {
    try {
      AppInfo appConfig = master.getAppInfo() ;
      AppMasterMonitor monitor = master.getAppMonitor() ;
      int complete = monitor.getCompletedContainerCount().intValue() ;
      master.getAMRMClient().allocate(complete/(float)appConfig.appNumOfWorkers) ;
    } catch (Exception e) {
      LOGGER.error("onCompleteContainer() report error", e);
    }
    LOGGER.info("on complete container " + status.getContainerId());
  }

  public void onFailedContainer(AppMaster master, ContainerStatus status, AppWorkerContainerInfo containerInfo) {
    LOGGER.info("on failed container " + status.getContainerId());
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

  public void waitForComplete(AppMaster appMaster) {
    LOGGER.info("Start waitForComplete(AppMaster appMaster)");
    AppInfo appConfig = appMaster.getAppInfo() ;
    try {
      boolean finished = false ;
      while(!finished) {
        synchronized(this) {
          this.wait(500);
        } 
        AppMasterMonitor monitor = appMaster.getAppMonitor() ;
        AppWorkerContainerInfo[] cinfos = monitor.getContainerInfos() ;
        if(cinfos.length < appConfig.appNumOfWorkers)  continue ;
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
}