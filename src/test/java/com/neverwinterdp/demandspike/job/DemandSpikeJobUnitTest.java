package com.neverwinterdp.demandspike.job;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.demandspike.DemandSpikeClusterBuilder;
import com.neverwinterdp.server.shell.Shell;
import com.neverwinterdp.util.IOUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class DemandSpikeJobUnitTest {
  static DemandSpikeClusterBuilder clusterBuilder ;
  static protected Shell shell ;
  static String TOPIC = DemandSpikeClusterBuilder.TOPIC ;
  
  @BeforeClass
  static public void setup() throws Exception {
    clusterBuilder = new DemandSpikeClusterBuilder() ;
    clusterBuilder.start() ;
    shell = clusterBuilder.shell ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    clusterBuilder.destroy() ; 
  }
 
  @Test
  public void testDemandSpikeJobCommands() throws Exception {
    clusterBuilder.install() ; 
    Thread.sleep(10000);
    shell.execute("demandspike:job send --max-num-of-message 1000");
    shell.execute("demandspike:job send --driver kafka --broker-connect 127.0.0.1:9092 --topic metrics.consumer --max-num-of-message 1000");
    shell.execute(
        "demandspike:job simulation " +
        "  --name service-failure --target-member-role kafka " + 
        "  --module Kafka --service-id KafkaClusterService --delay 0 --period 5000 --failure-time 1000");
    Thread.sleep(3000);
    clusterBuilder.uninstall();
  }
  
  @Test
  public void testDemandSpikeJobService() throws Exception {
    String json = IOUtil.getFileContentAsString("src/test/resources/demandspikejob.json") ;
    clusterBuilder.install() ; 
    Thread.sleep(3000);
    shell.execute("demandspike submit --member-name demandspike #{data " + json + " }#");
    //shell.execute("demandspike submit --member-name demandspike --file src/test/resources/demandspikejob.json");
    Thread.sleep(10000);
    shell.execute("demandspike scheduler --member-name demandspike");
    clusterBuilder.uninstall();
  }
}