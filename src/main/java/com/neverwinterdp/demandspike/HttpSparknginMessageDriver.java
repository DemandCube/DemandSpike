package com.neverwinterdp.demandspike;

import io.netty.handler.codec.http.HttpResponse;

import java.util.List;

import com.codahale.metrics.Timer;
import com.neverwinterdp.message.Message;
import com.neverwinterdp.netty.http.client.HttpClient;
import com.neverwinterdp.netty.http.client.ResponseHandler;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitor;

public class HttpSparknginMessageDriver implements MessageDriver {
  private ApplicationMonitor appMonitor ;
  private ComponentMonitor   driverMonitor ;
  private String topic ;
  private HttpClient client ;
  
  public HttpSparknginMessageDriver(ApplicationMonitor appMonitor) {
    this.appMonitor = appMonitor ;
    driverMonitor   = appMonitor.createComponentMonitor(HttpSparknginMessageDriver.class) ;
  }
  
  public void init(List<String> connect, String topic) {
    this.topic = topic ;
    ResponseHandler handler = new ResponseHandler() {
      public void onResponse(HttpResponse response) {
      }
    };
    try {
      for(String selConnect : connect) {
        int separatorIdx = selConnect.lastIndexOf(":") ;
        String host = selConnect.substring(0, separatorIdx) ;
        int port = Integer.parseInt(selConnect.substring(separatorIdx + 1));
        client = new HttpClient (host, port, handler) ;
      }
    } catch(Exception ex) {
      throw new RuntimeException("Sparkngin Driver Error", ex) ;
    }
  }
  
  public void send(Message message) throws Exception {
    Timer.Context ctx = driverMonitor.timer("send(Message)").time() ;
    message.getHeader().setTopic(topic);
    client.post("/message", message);
    ctx.stop() ;
  }
  
  public void close() { 
    client.close();
  }
}