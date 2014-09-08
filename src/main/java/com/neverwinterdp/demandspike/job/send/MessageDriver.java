package com.neverwinterdp.demandspike.job.send;

import java.util.List;
import java.util.Map;

import com.neverwinterdp.demandspike.constants.Method;
import com.neverwinterdp.demandspike.http.Message;


public interface MessageDriver {
  public void init(Map<String, String> props, List<String> connect, String topic) ;
  public void init(List<String> connect, Method method,String topic) ;
  public void send(Message message) throws Exception ;
  public void close() ;
}