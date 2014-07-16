package com.neverwinterdp.demandspike.yarn;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.demandspike.DemandSpikeClusterBuilder;
import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;
import com.neverwinterdp.hadoop.yarn.app.AppClient;
import com.neverwinterdp.hadoop.yarn.app.AppClientMonitor;
import com.neverwinterdp.server.shell.Shell;

public class DemandSpikeAppContainerUnitTest extends AbstractMiniClusterUnitTest {
  static MiniYARNCluster miniYarnCluster ;
  static DemandSpikeClusterBuilder clusterBuilder ;
  static protected Shell shell ;
  
  @BeforeClass
  static public void setup() throws Exception {
    clusterBuilder = new DemandSpikeClusterBuilder() ;
    clusterBuilder.start() ;
    clusterBuilder.install() ;
    shell = clusterBuilder.shell ;
    
    miniYarnCluster = createMiniYARNCluster(1);
    Thread.sleep(5000);
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
      "--app-name", "NeverwinterDP DemandSpike App",
      "--container-manager", "com.neverwinterdp.demandspike.yarn.AsyncDemandSpikeAppContainerManager",
      "--conf:yarn.resourcemanager.scheduler.address=0.0.0.0:8030",
      "--conf:demandspike.instance.core=1",
      "--conf:demandspike.instance.memory=128",
      "--conf:demandspike.job.num-of-task=3",
      "--conf:demandspike.job.driver=kafka",
      "--conf:demandspike.job.topic=" + DemandSpikeClusterBuilder.TOPIC ,
      "--conf:demandspike.job.broker-connect=" + clusterBuilder.getKafkaConnect(),
      "--conf:demandspike.job.max-duration=25000",
      "--conf:demandspike.job.requiredAcks=1",
      "--conf:demandspike.job.compressionCodec=snappy",
      "--conf:demandspike.job.sendBufferBytes=65536",
      "--conf:demandspike.job.producerType=async",
      "--conf:demandspike.job.batchNumMessages=200",
      "--conf:demandspike.job.enqueueTimeout=-1",
      "--conf:demandspike.job.clientId=producer",
      "--conf:demandspike.job.requestTimeout=6000",
      "--conf:demandspike.job.sendMaxRetries=3",
      "--conf:demandspike.job.retryBackoff=300"
    } ;
    
    AppClient appClient = new AppClient() ;
    AppClientMonitor appMonitor = 
        appClient.run(args, new YarnConfiguration(miniYarnCluster.getConfig()));
    appMonitor.monitor() ;
    appMonitor.report(System.out);
  }
}
