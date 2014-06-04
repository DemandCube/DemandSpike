package com.neverwinterdp.demandspike;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.shell.Shell;
import com.neverwinterdp.util.FileUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class DemandSpikeServiceUnitTest {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("app.config.dir", "src/app/config") ;
    System.setProperty("log4j.configuration", "file:src/app/config/log4j.properties") ;
  }
  
  static protected Server zkServer, kafkaServer, sparknginServer, demandSpikeServer ;
  static protected Shell shell ;
  static String TOPIC = "DemandSpike" ;
    
  @BeforeClass
  static public void setup() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    zkServer = Server.create("-Pserver.name=zookeeper", "-Pserver.roles=zookeeper") ;
    kafkaServer = Server.create("-Pserver.name=kafka", "-Pserver.roles=kafka") ;
    sparknginServer = Server.create("-Pserver.name=sparkngin", "-Pserver.roles=sparkngin") ;
    demandSpikeServer = Server.create("-Pserver.name=demandspike", "-Pserver.roles=demandspike") ;
    shell = new Shell() ;
    shell.getShellContext().connect();
    //Wait to make sure all the servervices are launched
    Thread.sleep(2000) ;
  }

  @AfterClass
  static public void teardown() throws Exception {
    shell.close();
    demandSpikeServer.destroy() ;
    sparknginServer.destroy() ;
    kafkaServer.destroy();
    zkServer.destroy() ;
  }
  
  @Test
  public void testSendMessage() throws Exception {
    install() ;
    shell.execute(
      "demandspike submit " + 
      "  --driver kafka --connect-url 127.0.0.1:9092 --topic " + TOPIC +
      "  --member-role demandspike --max-duration 1000"
    );
    Thread.sleep(1000);
    uninstall(); 
  }
  
  private void install() throws InterruptedException {
    String installScript =
        "module install " + 
        " -Pmodule.data.drop=true" +
        " --member-role zookeeper --autostart Zookeeper \n" +
        
        "module install " +
        " -Pmodule.data.drop=true" +
        " -Pkafka.zookeeper-urls=127.0.0.1:2181" +
        " -Pkafka.consumer-report.topics=" + TOPIC +
        "  --member-role kafka --autostart Kafka \n" +
        
        "module install " + 
        "  --member-role sparkngin --autostart Sparkngin \n" +
        
        "module install " +  
        " --member-role demandspike --autostart DemandSpike \n" +
        
        "service registration" ;
    shell.executeScript(installScript);
    Thread.sleep(1000);
  }
  
  void uninstall() {
    String uninstallScript = 
        "module uninstall --member-role demandspike --timeout 20000 DemandSpike \n" +
        "module uninstall --member-role sparkngin --timeout 20000 Sparkngin \n" +
        "module uninstall --member-role kafkar --timeout 20000 Kafka \n" +
        "module uninstall --member-role zookeeper --timeout 20000 Zookeeper";
    shell.executeScript(uninstallScript);
  }
}