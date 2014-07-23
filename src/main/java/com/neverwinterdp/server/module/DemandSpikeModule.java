package com.neverwinterdp.server.module;

import java.util.Map;

import com.neverwinterdp.demandspike.DemandSpikeClusterService;
import com.neverwinterdp.demandspike.job.DemandSpikeJobService;

@ModuleConfig(name = "DemandSpike", autostart = false, autoInstall=false)
public class DemandSpikeModule extends ServiceModule {
  
  protected void configure(Map<String, String> properties) {  
    bindService(DemandSpikeClusterService.class) ;
    bindService(DemandSpikeJobService.class) ;
  }

}