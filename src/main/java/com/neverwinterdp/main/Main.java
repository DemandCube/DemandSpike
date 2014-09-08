package com.neverwinterdp.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.demandspike.job.DemandSpikeJob;
import com.neverwinterdp.demandspike.job.JobConfig;
import com.neverwinterdp.demandspike.job.send.MessageDriverConfig;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.snapshot.ApplicationMonitorSnapshot;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;
import com.neverwinterdp.util.monitor.snapshot.TimerSnapshot;

public class Main {

	public static void main(String[] args) throws IOException {
		  List<String> connect = new ArrayList<String>();
		  
		  connect.add("http://www.yahoo.com");
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
