package com.neverwinterdp.demandspike.job.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemandSpikeJob implements Serializable {
  private String id ;
  private String description ;
  private List<DemandSpikeTask> tasks ;
  private Map<String, Object> outputAttributes = new HashMap<String, Object>() ;
  
  public String getId() { return this.id ; }
  public void setId(String id) { this.id = id ; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) {
    this.description = description;
  }
  
  public List<DemandSpikeTask> getTasks() { return tasks; }
  public void setTasks(List<DemandSpikeTask> tasks) {
    this.tasks = tasks;
  }
  
  public Map<String, Object> getOutputAttributes() {
    return outputAttributes;
  }
  
  public void setOutputAttributes(Map<String, Object> outputAttributes) {
    this.outputAttributes = outputAttributes;
  }
  
  public Object getOutputAttribute(String name) {
    return outputAttributes.get(name) ;
  }
  
  public void setOutputAttribute(String name, Object attribute) {
    outputAttributes.put(name, attribute) ;
  }
}