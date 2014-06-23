package com.neverwinterdp.demandspike;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.shell.Shell;
import com.neverwinterdp.util.FileUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class DemandSpikeClusterBuilder {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("app.config.dir", "src/app/config") ;
    System.setProperty("log4j.configuration", "file:src/app/config/log4j.properties") ;
  }
  
  public static String TOPIC = "metrics.consumer" ;
  
  public Server zkServer, kafkaServer, sparknginServer, demandSpikeServer ;
  public Shell shell ;
  

  public void start() throws Exception {
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
  
  public void destroy() throws Exception {
    shell.close();
    demandSpikeServer.destroy() ;
    sparknginServer.destroy() ;
    kafkaServer.destroy();
    zkServer.destroy() ;
  }
 
  public void install() throws InterruptedException {
    String installScript =
        "module install " + 
        " -Pmodule.data.drop=true" +
        " --member-role zookeeper --autostart --module Zookeeper \n" +
        
        "module install " +
        " -Pmodule.data.drop=true" +
        " -Pkafka.zookeeper-urls=127.0.0.1:2181" +
        "  --member-role kafka --autostart --module Kafka \n" +
        
        "module install " + 
        "  -Phttp-listen-port=8181 " +
        "  -Pqueue-buffer=3000 " +
        "  --member-role sparkngin --autostart --module Sparkngin \n" +
        
        
        "module install " +  
        " --member-role demandspike --autostart --module DemandSpike \n" +
        
        "service registration" ;
    shell.executeScript(installScript);
    Thread.sleep(1000);
  }
  
  public void uninstall() {
    String uninstallScript = 
        "module uninstall --member-role demandspike --timeout 20000 --module DemandSpike \n" +
        "module uninstall --member-role sparkngin --timeout 20000 --module Sparkngin \n" +
        "module uninstall --member-role kafka --timeout 20000 --module Kafka \n" +
        "module uninstall --member-role zookeeper --timeout 20000 --module Zookeeper";
    shell.executeScript(uninstallScript);
  }
}