package com.neverwinterdp.demandspike;

import java.util.List;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.queuengin.kafka.KafkaMessageProducer;
import com.neverwinterdp.util.text.StringUtil;

public class KafkaMessageDriver implements MessageDriver {
  private String topic ;
  private KafkaMessageProducer producer ;
  
  public KafkaMessageDriver() {
  }
  
  public void init(List<String> connect, String topic) {
    this.topic = topic ;
    String connectUrls = StringUtil.join(connect, ",") ;
    producer = new KafkaMessageProducer(connectUrls) ;
  }
  
  public void send(Message message) throws Exception {
    message.getHeader().setTopic(topic);
    producer.send(topic,  message) ;
    System.out.println("Kafka Sent: " + message.getHeader().getKey());
  }
  
  public void close() { 
    producer.close();
  }
}
