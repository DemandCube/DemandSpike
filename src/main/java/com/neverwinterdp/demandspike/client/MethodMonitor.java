package com.neverwinterdp.demandspike.client;

public class MethodMonitor {
  private String method;
  private int    count                           = 0;
  private int    responseCount                   = 0;
  private int    clientLimitTimeoutCount         = 0;
  private int    closeChannelExceptionCount      = 0;
  private int    connectionTimeoutExceptionCount = 0;
  private int    timeoutExceptionCount           = 0;
  private int    unknownErrorCount               = 0;
  private long   sumAvgExecutionTime             = 0;
  
  public MethodMonitor() {}
  
  public MethodMonitor(String method) {
    this.method = method ;
  }
  
  public String getMethod() { return method; }
  public void setMethod(String method) { this.method = method; }
  
  public int  getCount() { return count; }
  public void setCount(int count) { this.count = count; }
  public void incrCount() { count++ ; }
  
  public int getResponseCount() { return responseCount; }
  public void setResponseCount(int responseCount) {
    this.responseCount = responseCount;
  }
  public void incrResponseCount() { responseCount++ ; }
  
  public int  getClientLimitTimeoutCount() { return clientLimitTimeoutCount; }
  public void setClientLimitTimeoutCount(int timeoutCount) { this.clientLimitTimeoutCount = timeoutCount; }
  public void incrClientLimitTimeoutCount() { this.clientLimitTimeoutCount++; }
  
  public int getCloseChannelExceptionCount() { return closeChannelExceptionCount; }
  public void setCloseChannelExceptionCount(int closeChannelExceptionCount) {
    this.closeChannelExceptionCount = closeChannelExceptionCount;
  }
  public void incrCloseChannelExceptionCount() { this.closeChannelExceptionCount++; }
  
  public int getConnectionTimeoutExceptionCount() { return connectionTimeoutExceptionCount; }
  public void setConnectionTimeoutExceptionCount(int connectionTimeoutExceptionCount) {
    this.connectionTimeoutExceptionCount = connectionTimeoutExceptionCount;
  }
  
  public void incConnectionTimeoutExceptionCount() { this.connectionTimeoutExceptionCount++; }
  
  public int getTimeoutExceptionCount() { return timeoutExceptionCount; }
  public void setTimeoutExceptionCount(int timeoutExceptionCount) {
    this.timeoutExceptionCount = timeoutExceptionCount;
  }
  
  public void incrTimeoutExceptionCount() { this.timeoutExceptionCount++; }
  
  public int getUnknownErrorCount() { return unknownErrorCount; }
  public void setUnknownErrorCount(int unknownErrorCount) {
    this.unknownErrorCount = unknownErrorCount;
  }
  public void incrUnknownErrorCount() {
    this.unknownErrorCount++;
  }

  public long getSumAvgExecutionTime() { return sumAvgExecutionTime; }
  public void setSumAvgExecutionTime(long sumAvgExecutionTime) {
    this.sumAvgExecutionTime = sumAvgExecutionTime;
  }
  
  public void addSumAvgExecutionTime(long execTime) {
    this.sumAvgExecutionTime = execTime;
  }
  
  static MethodMonitor merge(MethodMonitor methodMonitor) {
    MethodMonitor mMonitor = new MethodMonitor() ;
    //TODO: merge the method monitor data
    return mMonitor; 
  }
}
