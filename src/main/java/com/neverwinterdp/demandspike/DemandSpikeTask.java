package com.neverwinterdp.demandspike;

import java.io.Serializable;

import org.slf4j.Logger;

abstract public class DemandSpikeTask implements Runnable, Serializable {
  
  Logger logger ;
  String taskId;
  long   maxDuration ;
  long   maxNumOfMessage ;
  MessageGenerator        messageGenerator ;
  protected MessageDriver messageDriver ;
  
  public void setLogger(Logger logger) {
    this.logger = logger ;
  }
  
  public String getTaskId() { return taskId; }
  public void setTaskId(String id) {
    this.taskId = id ;
  }
  
  public void setMaxDuration(long max) {
    this.maxDuration = max ;
  }
  
  public void setMaxNumOfMessage(long max) {
    this.maxNumOfMessage = max ;
  }
  
  public void setDriver(MessageDriver driver) {
    this.messageDriver = driver ;
  }
  
  public void setMessageGenerator(MessageGenerator generator) {
    this.messageGenerator = generator ;
  }
  
  public void onFinish() {
    messageDriver.close() ; 
  }
}
