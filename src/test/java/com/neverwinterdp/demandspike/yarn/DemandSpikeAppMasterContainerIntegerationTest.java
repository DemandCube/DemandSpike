package com.neverwinterdp.demandspike.yarn;

import org.junit.Test;

import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;

public class DemandSpikeAppMasterContainerIntegerationTest  {
  @Test
  public void testDemandSpikeApp() throws Exception {
    String[] args = { 
      "--app-home", "/tmp/app/DemandSpike",
      "--app-home-local", "./build/DemandSpike"  ,
      "--app-name", "NeverwinterDP_DemandSpike_App",
      "--app-container-manager", "com.neverwinterdp.demandspike.yarn.master.AsyncDemandSpikeAppMasterContainerManager",
      "--app-rpc-port", "63200" ,
      "--app-history-server-address", "http://192.168.1.30:8080/yarn-app/history",
      "--conf:fs.default.name=hdfs://hadoop:9000",
      "--conf:dfs.replication=1",
      "--conf:yarn.resourcemanager.scheduler.address=hadoop:8030",
      "--conf:yarn.resourcemanager.address=hadoop:8032",
      "--conf:broker-connect=192.168.1.30:7080",
      "--conf:max-duration=5000"
    } ;
    
    AppClient appClient = new AppClient() ;
    AppClientMonitor appMonitor = appClient.run(args);
    appMonitor.monitor() ;
    appMonitor.report(System.out);
    Thread.sleep(3000);
  }
}
