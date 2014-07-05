package com.neverwinterdp.demandspike;

import com.neverwinterdp.server.gateway.ClusterGateway;


public interface ProblemSimulator {
  
  public void onInit(ClusterGateway cluster) ;
  
  public void start();
  public void stop() ;
}
