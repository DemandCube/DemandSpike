package com.neverwinterdp.demandspike.client;

import java.util.HashMap;
import java.util.Map;

public class Monitor {
  private Map<String, MethodMonitor> monitors = new HashMap<String, MethodMonitor>() ;

  public int count() {
    int count = 0;
    for(MethodMonitor sel : monitors.values()) count += sel.getCount() ;
    return count ;
  }
  
  public int responseCount() {
    int responseCount = 0;
    for(MethodMonitor sel : monitors.values()) responseCount += sel.getResponseCount() ;
    return responseCount ;
  }
  
  public int clientLimitTimeoutCount() {
    int timeoutCount = 0;
    for(MethodMonitor sel : monitors.values()) timeoutCount += sel.getClientLimitTimeoutCount() ;
    return timeoutCount ;
  }
  
  public int closeChannelExceptionCount() {
    int count = 0;
    for(MethodMonitor sel : monitors.values()) count += sel.getCloseChannelExceptionCount() ;
    return count ;
  }
  
  public int timeoutCount() {
    int timeoutCount = 0;
    for(MethodMonitor sel : monitors.values()) timeoutCount += sel.getTimeoutExceptionCount() ;
    return timeoutCount ;
  }
  
  public MethodMonitor getMethodMonitor(String name) {
    MethodMonitor monitor = monitors.get(name) ;
    if(monitor == null) {
      monitor = new MethodMonitor(name) ;
      monitors.put(name, monitor) ;
    }
    return monitor ;
  }
  
  public MethodMonitor[] getRequestMonitors() {
    MethodMonitor[] array = new MethodMonitor[monitors.size()] ;
    monitors.values().toArray(array) ;
    return array ;
  }
  
  public void setRequestMonitors(MethodMonitor[] array) {
    for(MethodMonitor sel : array) {
      monitors.put(sel.getMethod(), sel);
    }
  }
  
  static Monitor merge(Monitor ... monitor) {
    Monitor newMonitor = new Monitor() ;
    //TODO: do the merge
    return newMonitor ;
  }
}
