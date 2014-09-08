package com.neverwinterdp.demandspike.job.send;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Timer;
import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.demandspike.http.HttpMessageClient;
import com.neverwinterdp.demandspike.http.Message;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitor;

public class HttpDriver implements MessageDriver {
 private static Logger logger;
  private ApplicationMonitor appMonitor ;
  private ComponentMonitor   driverMonitor ;
  private String topic = "";
  private HttpMessageClient client ;
  private Method method;
  private List<String> connect;
  
  
  public HttpDriver(ApplicationMonitor appMonitor) {
	logger = LoggerFactory.getLogger("HttpDriver");
    this.appMonitor = appMonitor ;
    driverMonitor   = appMonitor.createComponentMonitor(HttpDriver.class) ;
  }
  
  public void init(Map<String, String> props, List<String> connect, String topic) {
    this.topic = topic ;
    try {
      for(String selConnect : connect) {
        int separatorIdx = selConnect.lastIndexOf(":") ;
        String host = selConnect.substring(0, separatorIdx) ;
        int port = Integer.parseInt(selConnect.substring(separatorIdx + 1));
        client = new HttpMessageClient (host, port, 300) ;
      }
    } catch(Exception ex) {
      throw new RuntimeException("Sparkngin Driver Error", ex) ;
    }
  }
  
  
  public void init(List<String> connect, Method method, String topic) {
	  this.topic = topic ;
	  this.method = method;
	  this.connect = connect;
	    try {
	      for(String selConnect : connect) {

	    	URL url = new URL(selConnect);
	    	
	    	String host =  url.getHost();
	    	int port = 80;
	    	
	    	if(url.getPort() > 0){
	    		port = url.getPort();
	    	}
	    	logger.info("Connecting to "+host+":"+port);
	        client = new HttpMessageClient (host, port, 300) ;
	      }
	    } catch(Exception ex) {
	      throw new RuntimeException("Sparkngin Driver Error", ex) ;
	    }
  }
  
  public void send(Message message) throws Exception {
   
    if(this.method.equals(Method.POST)){
    	 Timer.Context ctx = driverMonitor.timer("send(Message)").time() ;
    	 message.getHeader().setTopic(topic);
    	 client.send(message, 15000);
    	 ctx.stop() ;
    }else{
    	//System.out.println(this.connect.get(0));
    	Timer.Context ctx = driverMonitor.timer("get").time() ;
    	client.get(this.connect.get(0));
    	ctx.stop() ;
    	    	
    }
    
  }
  
  public void close() { 
    client.close();
  }
}