package com.neverwinterdp.demandspike;

import java.util.List;

import com.neverwinterdp.message.Message;

public class DummyMessageDriver implements MessageDriver {
  
  public void init(List<String> connect, String topic) {
  }
  
  public void send(Message message) throws Exception {
    System.out.println("Send: " + message.getHeader().getKey()) ;
  }
  
  public void close() { }

}
