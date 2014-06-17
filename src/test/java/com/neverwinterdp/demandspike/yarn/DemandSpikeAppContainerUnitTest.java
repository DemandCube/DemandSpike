package com.neverwinterdp.demandspike.yarn;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;
import com.neverwinterdp.hadoop.yarn.AppClient;
import com.neverwinterdp.hadoop.yarn.AppClientMonitor;

public class DemandSpikeAppContainerUnitTest extends AbstractMiniClusterUnitTest {
  MiniYARNCluster miniYarnCluster ;

  @Before
  public void setup() throws Exception {
    miniYarnCluster = createMiniYARNCluster(1);
    Thread.sleep(1000);
    Configuration conf = miniYarnCluster.getConfig() ;
  }

  @After
  public void teardown() throws Exception {
    miniYarnCluster.stop();
    miniYarnCluster.close();
  }

  @Test
  public void testDemandSpikeApp() throws Exception {
    String[] args = { 
      "--mini-cluster-env",
      "--app-name", "NeverwinterDP DemandSpike App",
      "--container-manager", "com.neverwinterdp.demandspike.yarn.DemandSpikeAppContainerManager",
      "--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
      "--conf:demandspike.instance.request=1",
      "--conf:demandspike.instance.core=1",
      "--conf:demandspike.instance.memory=128",
    } ;
    
    AppClient appClient = new AppClient() ;
    AppClientMonitor appMonitor = 
        appClient.run(args, new YarnConfiguration(miniYarnCluster.getConfig()));
    Thread.sleep(15000);
    appMonitor.report(System.out);
    appMonitor.kill() ; 
    Thread.sleep(1000);
  }
}