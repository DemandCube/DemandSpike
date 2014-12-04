package com.neverwinterdp.demandspike.yarn;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.demandspike.DemandSpikeClusterBuilder;
import com.neverwinterdp.hadoop.MiniClusterUtil;
import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;
import com.neverwinterdp.server.shell.Shell;

public class DemandSpikeAppMasterContainerUnitTest {
  static MiniYARNCluster miniYarnCluster ;
  static DemandSpikeClusterBuilder clusterBuilder ;
  static protected Shell shell ;
  
  @BeforeClass
  static public void setup() throws Exception {
    clusterBuilder = new DemandSpikeClusterBuilder() ;
    clusterBuilder.start() ;
    clusterBuilder.install() ;
    shell = clusterBuilder.shell ;
    
    miniYarnCluster = MiniClusterUtil.createMiniYARNCluster(1);
    Thread.sleep(1000);
    Configuration conf = miniYarnCluster.getConfig() ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    miniYarnCluster.stop();
    miniYarnCluster.close();
    
    clusterBuilder.destroy() ;
  }

  @Test
  public void testDemandSpikeApp() throws Exception {
    String[] args = { 
      "--mini-cluster-env",
      "--app-name", "NeverwinterDP_DemandSpike_App",
      "--app-container-manager", "com.neverwinterdp.demandspike.yarn.master.AsyncDemandSpikeAppMasterContainerManager",
      "--app-rpc-port", "63200" ,
      "--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
      "--conf:broker-connect=127.0.0.1:7080",
      "--conf:max-duration=5000"
    } ;
    
    AppClient appClient = new AppClient() ;
    AppClientMonitor appMonitor = 
        appClient.run(args, new YarnConfiguration(miniYarnCluster.getConfig()));
    appMonitor.monitor() ;
    appMonitor.report(System.out);
    Thread.sleep(3000);
    clusterBuilder.shell.exec("server metric");
  }
}
