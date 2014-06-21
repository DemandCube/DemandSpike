package com.neverwinterdp.demandspike;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.server.shell.Shell;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class DemandSpikeServiceUnitTest {
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
  public void testSendMessage() throws Exception {
    clusterBuilder.install() ; 
    shell.execute(
      "demandspike submit " + 
      "  --driver kafka --kafka-connect 127.0.0.1:9092 --topic " + TOPIC +
      "  --member-role demandspike --max-duration 30000 --max-num-of-message 100000" 
    );
    Thread.sleep(30000);
    shell.execute(
      "server metric --type timer --filter * " 
    );
    clusterBuilder.uninstall();
  }
}