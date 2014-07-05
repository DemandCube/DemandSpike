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
  public void testSparkngin() throws Exception {
    clusterBuilder.install() ; 
    shell.execute(
      "demandspike submit " + 
      "  --driver sparkngin --broker-connect 127.0.0.1:8181 --topic " + TOPIC +
      "  --member-role demandspike --max-duration 5000 --max-num-of-message 100000" 
    );
    Thread.sleep(10000);
    shell.execute(
      "server metric --filter * " 
    );
    clusterBuilder.uninstall();
  }
  
  @Test
  public void testKafka() throws Exception {
    clusterBuilder.install() ; 
    Thread.sleep(3000);
    shell.execute(
      "demandspike submit " + 
          "  --driver kafka --broker-connect " +  clusterBuilder.getKafkaConnect() + " --topic " + TOPIC +
      "  --member-role demandspike --max-duration 5000 --max-num-of-message 100000"
    );
    Thread.sleep(10000);
    shell.execute(
      "server metric --filter * " 
    );
    clusterBuilder.uninstall();
  }
  
  //@Test
  public void testKafkaRandomFailure() throws Exception {
    clusterBuilder.install() ; 
    Thread.sleep(3000);
    shell.execute(
      "demandspike submit " + 
      "  --driver kafka --broker-connect " +  clusterBuilder.getKafkaConnect() + " --topic " + TOPIC +
      "  --member-role demandspike --max-duration 60000 --max-num-of-message 100000" +
      //"  -Problem:kafka.description=\"kafka service randomly on/off\"" +
      "  -Problem:kafka.problem=service-failure" +
      "  -Problem:kafka.member-role=kafka" +
      "  -Problem:kafka.module=Kafka" +
      "  -Problem:kafka.service-id=KafkaClusterService" +
      "  -Problem:kafka.period=10000" +
      "  -Problem:kafka.failure-time=1000"
    );
    Thread.sleep(30000);
    shell.execute(
      "server metric --filter * " 
    );
    clusterBuilder.uninstall();
  }
}