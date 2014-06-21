package com.neverwinterdp.demandspike;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.neverwinterdp.util.monitor.ApplicationMonitor;

public class MessageDriverFactory implements Serializable {
  @Parameter(
    names = "--driver", 
    description = "The message driver to send the message. Either dummy, kafka or sparkngin"
  )
  String driver = "dummy" ;
  
  @Parameter(
    names = "--kafka-connect", variableArity= true, 
    description = "The connection url list"
  )
  List<String> connect  = new ArrayList<String>() ;
  
  @Parameter(
    names = "--topic", description = "The destination topic of the message"
  )
  String topic  ;
  
  public String getDriver() { return this.driver ; }
  
  public MessageDriver createDriver(ApplicationMonitor appMonitor) {
    MessageDriver mdriver = null ;
    if("kafka".equals(driver)) {
      mdriver = new KafkaMessageDriver(appMonitor) ;
    } else {
      mdriver = new DummyMessageDriver() ;
    }
    mdriver.init(connect, topic);
    return mdriver ;
  }
}