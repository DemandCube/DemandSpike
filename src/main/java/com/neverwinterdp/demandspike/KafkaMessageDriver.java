package com.neverwinterdp.demandspike;

import java.util.List;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.queuengin.kafka.KafkaMessageProducer;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitor;
import com.neverwinterdp.util.text.StringUtil;

public class KafkaMessageDriver implements MessageDriver {
  private ApplicationMonitor appMonitor ;
  private String topic ;
  private KafkaMessageProducer producer ;
  
  public KafkaMessageDriver(ApplicationMonitor appMonitor) {
    this.appMonitor = appMonitor ;
  }
  
  public void init(List<String> connect, String topic) {
    this.topic = topic ;
    String connectUrls = StringUtil.join(connect, ",") ;
    ComponentMonitor monitor = appMonitor.createComponentMonitor(KafkaMessageProducer.class) ;
    producer = new KafkaMessageProducer(monitor, connectUrls) ;
  }
  
  public void send(Message message) throws Exception {
    message.getHeader().setTopic(topic);
    producer.send(topic,  message) ;
  }
  
  public void close() { 
    producer.close();
  }
}
