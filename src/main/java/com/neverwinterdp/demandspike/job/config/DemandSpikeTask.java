package com.neverwinterdp.demandspike.job.config;

import java.io.Serializable;

public class DemandSpikeTask implements Serializable {
  private String description ;
  private String command ;
  
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getCommand() {
    return command;
  }
  public void setCommand(String command) {
    this.command = command;
  }
  
  
}
