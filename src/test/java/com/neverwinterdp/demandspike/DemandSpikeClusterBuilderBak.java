package com.neverwinterdp.demandspike;

import com.neverwinterdp.server.Server;
import com.neverwinterdp.server.gateway.ClusterGateway;
import com.neverwinterdp.server.gateway.CommandParams;
import com.neverwinterdp.server.shell.Shell;
import com.neverwinterdp.sparkngin.http.NullDevMessageForwarder;
import com.neverwinterdp.util.FileUtil;
/**
 * @author Tuan Nguyen
 * @email  tuan08@gmail.com
 */
public class DemandSpikeClusterBuilderBak {
  static {
    System.setProperty("app.dir", "build/cluster") ;
    System.setProperty("app.config.dir", "src/app/config") ;
    System.setProperty("log4j.configuration", "file:src/app/config/log4j.properties") ;
  }
  
  public static String TOPIC = "metrics.consumer" ;
  
  public Server zkServer, kafka1Server, kafka2Server, sparknginServer, demandSpikeServer ;
  public Shell shell ;
  public ClusterGateway gateway ;
  

  public void start() throws Exception {
    FileUtil.removeIfExist("build/cluster", false);
    zkServer = Server.create("-Pserver.name=zookeeper", "-Pserver.roles=zookeeper") ;
    
    kafka1Server = Server.create("-Pserver.name=kafka1", "-Pserver.roles=kafka") ;
    kafka2Server = Server.create("-Pserver.name=kafka2", "-Pserver.roles=kafka") ;
    
    sparknginServer = Server.create("-Pserver.name=sparkngin", "-Pserver.roles=sparkngin") ;
    
    demandSpikeServer = Server.create("-Pserver.name=demandspike", "-Pserver.roles=demandspike") ;
    
    gateway = new ClusterGateway() ;
    
    
    shell = new Shell() ;
    shell.getShellContext().connect();
    //Wait to make sure all the servervices are launched
    Thread.sleep(2000) ;
  }
  
  public void destroy() throws Exception {
    shell.close();
    demandSpikeServer.destroy() ;
    sparknginServer.destroy() ;
    kafka1Server.destroy();
    kafka2Server.destroy();
    zkServer.destroy() ;
  }
 
  public void install() throws Exception {
    gateway.module.execute(
        "install", 
        new CommandParams().
          field("member-role", "zookeeper").
          field("autostart", true).
          field("module", new String[] { "Zookeeper" }).
          field("-Pmodule.data.dro", true)
    ) ;
    
    String installScript =
        "module install " + 
        " -Pmodule.data.drop=true" +
        " --member-role zookeeper --autostart --module Zookeeper \n" +
        
        "module install " +
        " -Pmodule.data.drop=true" +
        " -Pkafka:broker.id=1" +
        " -Pkafka:port=9092" +
        " -Pkafka:zookeeper.connect=127.0.0.1:2181" +
        " -Pkafka:default.replication.factor=2" +
        " -Pkafka:controller.socket.timeout.ms=90000" +
        " -Pkafka:controlled.shutdown.enable=true" +
        " -Pkafka:controlled.shutdown.max.retries=3" +
        " -Pkafka:controlled.shutdown.retry.backoff.ms=60000" +
        " --member-name kafka1 --autostart --module Kafka \n" +
        
        "module install " +
        " -Pmodule.data.drop=true" +
        " -Pkafka:broker.id=2" +
        " -Pkafka:port=9093" +
        " -Pkafka.zookeeper.connect=127.0.0.1:2181" +
        " -Pkafka:default.replication.factor=2" +
        " -Pkafka:controller.socket.timeout.ms=90000" +
        " -Pkafka:controlled.shutdown.enable=true" +
        " -Pkafka:controlled.shutdown.max.retries=3" +
        " -Pkafka:controlled.shutdown.retry.backoff.ms=60000" +
        " --member-name kafka2 --autostart --module Kafka \n" +
        
        "module install " + 
        "  -Phttp-listen-port=8181 " +
        "  -Pforwarder-class=" + NullDevMessageForwarder.class.getName() +
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