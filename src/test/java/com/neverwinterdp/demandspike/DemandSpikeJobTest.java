package com.neverwinterdp.demandspike;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.demandspike.http.Message;
import com.neverwinterdp.demandspike.job.DemandSpikeJob;
import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.job.send.MessageDriverConfig;
import com.neverwinterdp.demandspike.yarn.worker.MessageSender;
import com.neverwinterdp.hadoop.AbstractMiniClusterUnitTest;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;

public class DemandSpikeJobTest extends AbstractMiniClusterUnitTest {

  

  @Test
  public void testMessageSender() throws Exception {
	  List<String> connect = new ArrayList<String>();
	  
	  connect.add("http://127.0.0.1:7080");
	  Method method = Method.GET;
	  JobConfig config;
	  if(method.equals(Method.POST)){
		  config = new JobConfig(new MessageDriverConfig("sparkngin", connect,"metrics.consumer",method), 1, 1024, 30000, 100000, 0);
	  }else{
		  config = new JobConfig(new MessageDriverConfig(connect,method), 1, 1024, 30000, 1000, 0); 
	  }
      ApplicationMonitor appMonitor = new ApplicationMonitor() ;
      DemandSpikeJob job = new DemandSpikeJob("sparkngin producer",appMonitor, config) ;
      job.run();
      ApplicationMonitorSnapshot snapshot = appMonitor.snapshot() ;
      Map<String, TimerSnapshot> timers = snapshot.getRegistry().getTimers() ;
      MetricFormater formater = new MetricFormater("  ") ;    
      System.out.println(formater.format(timers));

  }
}