package com.neverwinterdp.demandspike;

import org.junit.Test;
import com.neverwinterdp.demandspike.http.Message;
import com.neverwinterdp.demandspike.yarn.worker.MessageSender;

public class MessageSenderUnitTest {

  

  @Test
  public void testMessageSender() throws Exception {
    MessageSender sender = new MessageSender("127.0.0.1:7080", 10) ;
    for(int i = 0; i < 100; i++) {
      Message message = new Message("m" + i, "message " + i, true) ;
      sender.send(message, 3000);
    }
    Thread.sleep(1000);
    sender.close() ; 

  }
}