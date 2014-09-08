package com.neverwinterdp.demandspike.yarn.worker;

import java.util.Map;


public class DemandSpikeAppWorkerConfig {
  private String brokerConnect ;
  private String topic       ;
  private long   maxDuration ;
  private int    numOfMessages ;
  private int messageSize;
  


public DemandSpikeAppWorkerConfig() {} 
  
  public DemandSpikeAppWorkerConfig(Map<String, String> conf) {
    this.brokerConnect = getString(conf, "broker-connect", "127.0.0.1:8080") ;
    this.topic = getString(conf, "topic", "metrics.consumer") ;
    this.numOfMessages = getInt(conf, "num-of-message", 1000000) ;
    this.maxDuration = getLong(conf, "max-duration", (3 * 60 * 1000l)) ;
    this.messageSize = getInt(conf, "message-size", 1024) ;
  }
  public int getMessageSize() {
	return messageSize;
}

public void setMessageSize(int messageSize) {
	this.messageSize = messageSize;
}
  public String getBrokerConnect() { return brokerConnect ; }
  
  public String getTopic() { return topic; }

  public long getMaxDuration() { return maxDuration; }

  public int getNumOfMessages() { return numOfMessages; }

  private String getString(Map<String, String> map, String name, String defaultValue) {
    String val = map.get(name) ;
    if(val == null) return defaultValue ;
    return val ;
  }
  
  private int getInt(Map<String, String> map, String name, int defaultValue) {
    String val = map.get(name) ;
    if(val == null) return defaultValue ;
    return Integer.parseInt(val) ;
  }
  
  private long getLong(Map<String, String> map, String name, long defaultValue) {
    String val = map.get(name) ;
    if(val == null) return defaultValue ;
    return Long.parseLong(val) ;
  }
}