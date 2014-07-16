package com.neverwinterdp.demandspike;

import java.util.List;

import com.neverwinterdp.message.Message;

public interface MessageDriver {
  public void init(List<String> connect, String topic, MessageDriverConfig messageDriverConfig) ;
  public void send(Message message) throws Exception ;
  public void close() ;
}