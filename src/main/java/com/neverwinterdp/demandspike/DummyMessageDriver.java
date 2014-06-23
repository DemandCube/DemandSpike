package com.neverwinterdp.demandspike;

import java.util.List;

import com.neverwinterdp.message.Message;

public class DummyMessageDriver implements MessageDriver {
  private int count ;
  
  public void init(List<String> connect, String topic) {
  }
  
  public void send(Message message) throws Exception {
    count++ ;
    if(count > 0 &&count % 1000 == 0) {
      System.out.println("Sent  " + count + " messages") ;
    }
  }
  
  public void close() { 
    System.out.println("Sent  " + count + " messages") ;
  }

}
