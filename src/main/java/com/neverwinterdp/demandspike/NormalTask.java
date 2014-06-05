package com.neverwinterdp.demandspike;

import java.nio.channels.ClosedByInterruptException;

import com.neverwinterdp.message.Message;

public class NormalTask extends DemandSpikeTask {
  public void run() {
    long stopTime = System.currentTimeMillis() + maxDuration ;
    try {
      for(long i = 0; i < maxNumOfMessage; i++) {
        Message message = messageGenerator.next();
        messageDriver.send(message);
        if(System.currentTimeMillis() > stopTime) break ;
      }
    } catch(ClosedByInterruptException ex) {
      logger.warn("Task is closed by the interruption: " + ex.getMessage()) ;
    } catch(Exception ex) {
      logger.error("Task Error", ex) ;
    } finally {
      onFinish() ;
    }
  }
}
